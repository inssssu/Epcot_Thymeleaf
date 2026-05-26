package com.example.epcot_thymeleaf.service;

import com.example.epcot_thymeleaf.dto.request.ReqUserEditDto;
import com.example.epcot_thymeleaf.entity.BoardEntity;
import com.example.epcot_thymeleaf.entity.UserEntity;
import com.example.epcot_thymeleaf.repository.BoardRepository;
import com.example.epcot_thymeleaf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {

  private final UserRepository userRepository;
  private final BoardRepository boardRepository;
  private final PasswordEncoder passwordEncoder;

  public UserEntity getUserByLoginId(String loginId) {
    return userRepository.findByUsername(loginId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다." + loginId));
  }

  public List<BoardEntity> getMyBoards(UserEntity author) {
    return boardRepository.findAllByAuthorOrderByIdDesc(author);
  }

  public Page<BoardEntity> getMyBoardsPage(Long userId, Pageable pageable) {

    return boardRepository.findAllByIdOrderById(userId, pageable);
  }

  @Transactional
  public void editUserInfo(String loginId, ReqUserEditDto userEditDTO) {
    UserEntity user = userRepository.findByUsername(loginId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

    String newPassword = userEditDTO.getNewPassword();
    String newPasswordCheck = userEditDTO.getNewPasswordCheck();

    if ((newPassword == null || newPassword.isBlank()) && (newPasswordCheck == null || newPasswordCheck.isBlank())) {
      return;
    }

    if (newPassword == null || !newPassword.equals(newPasswordCheck)) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
    }

    user.setPassword(passwordEncoder.encode(newPassword));
  }
}
