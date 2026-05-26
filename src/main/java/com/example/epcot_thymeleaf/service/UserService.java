package com.example.epcot_thymeleaf.service;

import com.example.epcot_thymeleaf.dto.request.ReqUserDto;
import com.example.epcot_thymeleaf.dto.request.ReqWithdrawalDto;
import com.example.epcot_thymeleaf.entity.*;
import com.example.epcot_thymeleaf.exception.WithdrawalProcessException;
import com.example.epcot_thymeleaf.repository.UserRepository;
import com.example.epcot_thymeleaf.repository.UserRoleRepository;
import com.example.epcot_thymeleaf.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserRoleRepository userRoleRepository;
  private final UserStatusRepository userStatusRepository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity user = userRepository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found : " + username));

    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(user.getRole().getRoleName()));

    return new User(user.getUsername(), user.getPassword(), authorities);
  }

  @Transactional
  public String join(ReqUserDto dto) {
    if (userRepository.existsByUsername(dto.getUsername())) {
      throw new RuntimeException("이미 존재하는 아이디입니다");
    }

    UserRoleEntity role = userRoleRepository.findByRoleName(dto.getRole().toLowerCase())
        .orElseThrow(() -> new RuntimeException("선택한 권한(" + dto.getRole() + ")이 존재하지 않습니다."));

    UserStatusEntity status = userStatusRepository.findByStatusName("active")
        .orElseThrow(() -> new RuntimeException("기본 상태값('active')을 DB에서 찾을 수 없습니다."));

    UserEntity user = UserEntity.builder()
      .username(dto.getUsername())
      .password(passwordEncoder.encode(dto.getPassword()))
      .role(role)
      .status(status)
      .build();

    UserEntity savedUser = userRepository.save(user);

    System.out.println("user Id : " + savedUser.getId());

    return "success";
  }

  public @Nullable Object findAllUsers() {

    // 탈퇴 요청일이 오래된 순으로 정렬 필요. 없다면?
    return userRepository.findAllByOrderById();
  }

  public Optional<UserEntity> findById(Long userId) {

    return userRepository.findById(userId);
  }

  // 사용자 탈퇴 신청
  @Transactional
  public void requestWithdrawal(Long userId, ReqWithdrawalDto withdrawalDto) {

    try {
      // 사용자 존재 여부 확인 및 조회
      UserEntity user = userRepository.findById(userId)
          .orElseThrow(() -> new RuntimeException("해당 ID의 사용자를 찾을 수 없습니다. : " + userId));

      // DB 에서 'withdrawal_req' 상태 엔티티 조회
      // 이전에 DB 스크립트로 넣은 이름('withdrawal_req') 와 일치해야 함
      UserStatusEntity withdrawalStatus = userStatusRepository.findByStatusName("withdrawal_req")
          .orElseThrow(() -> new RuntimeException("탈퇴 요청 상태('withdrawal_req')를 DB에서 찾을 수 없습니다."));

      // 사용자 상태 변경 및 탈퇴 요청 시간 기록
      user.setStatus(withdrawalStatus);
      user.setWithdrawalRequestedAt(LocalDateTime.now());

      if (withdrawalDto.getWithdrawalReason() == null || withdrawalDto.getWithdrawalReason().isBlank()) {
        throw new WithdrawalProcessException("탈퇴 사유를 입력해야 합니다.");
      }

      user.setWithdrawalReason(withdrawalDto.getWithdrawalReason());

    } catch (Exception e) {
      throw new WithdrawalProcessException(e.getMessage());
    }
  }

  // 사용자 탈퇴 신청 취소
  @Transactional
  public void cancelWithdrawal(Long userId) {
    // 사용자 조회
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("해당 ID 의 사용자를 찾을 수 없습니다."));

    // 현재 상태가 'withdrawal_req' (탈퇴 신청) 인지 확인
    // 객체간의 비교가 아닌, DB 에 저장된 상태 명칭(String) 으로 비교
    if (user.getStatus() != null && "withdrawal_req".equals(user.getStatus().getStatusName())) {

      // 'active' (정상) 상태 엔티티 조회
      UserStatusEntity activeStatus = userStatusRepository.findByStatusName("active")
          .orElseThrow(() -> new RuntimeException("정상 상태('active') 를 DB 에서 찾을 수 없습니다."));

      // 상태 복구 및 요청 시간 초기화
      user.setStatus(activeStatus);
      user.setWithdrawalRequestedAt(null);

    } else {
      throw new RuntimeException("탈퇴 신청 대기 상태인 사용자만 취소가 가능합니다.");
    }

  }

  // 관리자 탈퇴 승인
  @Transactional
  public void completeWithdrawal(Long targetUserId, String adminRoleName) {

    // 탈퇴 대상 사용자 조회
    UserEntity user = userRepository.findById(targetUserId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. : " + targetUserId));

    // 현재 상태 확인
    String currentStatus = user.getStatus() != null ? user.getStatus().getStatusName() : "";
    String nextStatusName = "";

    // 현재 상태와 관리자 권한에 따른 다음 단계 설정
    switch (currentStatus) {
      case "withdrawal_req":
        // 1단계 승인 (일반 관리자, 마스터 관리자 모두 승인 가능)
        if (!adminRoleName.equals("admin") && !adminRoleName.equals("master_admin")) {
          throw new RuntimeException("1차 승인 권한이 없습니다");
        }
        nextStatusName = "stage_1_approved";
        break;

      case "stage_1_approved":
        if (!adminRoleName.equals("admin") && !adminRoleName.equals("master_admin")) {
          throw new RuntimeException("2차 승인 권한이 없습니다");
        }
        nextStatusName = "stage_2_approved";
        break;

      case "stage_2_approved":
        if (!adminRoleName.equals("master_admin")) {
          throw new RuntimeException("최종 승인은 마스터 관리자만 가능합니다");
        }
        nextStatusName = "stage_3_final";
        break;

      case "stage_3_final":
        throw new RuntimeException("이미 최종 탈퇴 처리가 완료된 사용자입니다");

      default:
        throw new RuntimeException("탈퇴 승인을 진행할 수 없는 상태입니다. (현재 상태 : " + currentStatus + ")");
    }

    // 다음 단계 상태 엔티티 조회
    String finalNextStatusName = nextStatusName;
    UserStatusEntity nextStatus = userStatusRepository.findByStatusName(nextStatusName)
        .orElseThrow(() -> new RuntimeException("상태값('" + finalNextStatusName + "') 을 DB에서 찾을 수 없습니다."));

    user.setStatus(nextStatus);
    user.setWithdrawalResultShown(true);
  }

  // 관리자 탈퇴 반려
  @Transactional
  public void rejectWithdrawal(Long userId, String reason) {

    // 대상 사용자 조회
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다"));

    // 'active' 상태 엔티티 조회
    UserStatusEntity activeStatus = userStatusRepository.findByStatusName("active")
        .orElseThrow(() -> new RuntimeException("저상 상태('active') 를 DB 에서 찾을 수 없습니다"));

    // 사용자 상태 복구 및 반려 데이터 세팅
    user.setStatus(activeStatus);
    user.setWithdrawalRequestedAt(null);

    // 반려 사유 저장
    user.setWithdrawalRejectionReason(reason);

    user.setWithdrawalResultShown(false);
  }

  // 계정 정지 처리
  @Transactional
  public void suspendUser(Long userId) {

    // 대상 사용자 조회
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다"));

    UserStatusEntity suspendedStatus = userStatusRepository.findByStatusName("suspended")
        .orElseThrow(() -> new RuntimeException("정지 상태 ('suspended') 를 DB에서 찾을 수 없습니다"));

    user.setStatus(suspendedStatus);
  }

  @Transactional
  public void moveToSecondProcess(Long userId, String adminMemo) {

  }
}





















