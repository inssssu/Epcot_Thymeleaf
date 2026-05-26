package com.example.epcot_thymeleaf.service;

import com.example.epcot_thymeleaf.dto.request.ReqAdminUserDto;
import com.example.epcot_thymeleaf.entity.UserEntity;
import com.example.epcot_thymeleaf.entity.UserStatusEntity;
import com.example.epcot_thymeleaf.repository.UserRepository;
import com.example.epcot_thymeleaf.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WithdrawalService {

  private final UserRepository userRepository;
  private final AdminLogService adminLogService;
  private final UserStatusRepository userStatusRepository;
  private final UserService userService;

  // 반려처리
  @Transactional
  public void rejectWithdrawal(Long userId, ReqAdminUserDto adminUserDto, String reason, String ip) {

    UserEntity targetUser = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

    UserStatusEntity activeStatus = userStatusRepository.findByStatusName("active")
        .orElseThrow(() -> new RuntimeException("저장 상태('active') 를 DB 에서 찾을 수 없습니다."));

    targetUser.setStatus(activeStatus);
    targetUser.setWithdrawalRequestedAt(null);

    targetUser.setWithdrawalRejectionReason(reason);
    targetUser.setWithdrawalResultShown(false);

    adminLogService.saveLog(
        adminUserDto,
        targetUser,
        "REJECT_WITHDRAWAL",
        "반려사유 : " + reason,
        ip);
  }

  // 1단계 승인 및 2단계 이동
  @Transactional
  public void approveFirstStep(Long userId, ReqAdminUserDto admin, String memo, String ip) {

    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    adminLogService.saveLog(admin, user, "stage_2_approved", "1단계 검토 의견 : " + memo, ip);

  }

  @Transactional
  public void updateFirstAdminMemo(Long userId, String memo, ReqAdminUserDto adminUserDto, String ip) {

    UserEntity targetUser = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

    targetUser.setFirstAdminMemo(memo);

    adminLogService.saveLog(
        adminUserDto,
        targetUser,
        "MEMO_UPDATE",
        "1차 관리자 메모 수정 : " + memo,
        ip
    );
  }

  @Transactional
  public void updateSecondAdminMemo(Long userId, String memo, ReqAdminUserDto adminUserDto, String ip) {

    UserEntity targetUser = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

    targetUser.setSecondAdminMemo(memo);

    adminLogService.saveLog(
        adminUserDto,
        targetUser,
        "MEMO_UPDATE",
        "2차 관리자 메모 수정 : " + memo,
        ip
    );
  }

  @Transactional
  public void approveWithdrawal(Long userId, String finalMemo, ReqAdminUserDto adminUserDto, String ip) {

    UserEntity targetUser = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

    targetUser.setSecondAdminMemo(finalMemo);

//    adminUserDto.setId(user.getId());
//    adminUserDto.setUsername(user.getUsername());

    UserStatusEntity withdrawnStatus = userStatusRepository.findByStatusName("stage_3_final")
        .orElseThrow(() -> new RuntimeException("탈퇴 완료 상태 코드를 찾을 수 없습니다"));
    targetUser.setStatus(withdrawnStatus);

    adminLogService.saveLog(
        adminUserDto,
        targetUser,
        "APPROVE_WITHDRAWAL",
        "회원탈퇴 최종 승인 및 메모 : " + finalMemo,
        ip
    );

    // 탈퇴 완료 시점도 구성 필요한지
  }

}





















