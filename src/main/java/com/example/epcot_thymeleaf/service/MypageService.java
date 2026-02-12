package com.example.epcot_thymeleaf.service;

import com.example.epcot_thymeleaf.dto.request.UserEditDTO;
import com.example.epcot_thymeleaf.entity.BoardEntity;
import com.example.epcot_thymeleaf.entity.UserEntity;
import com.example.epcot_thymeleaf.repository.BoardRepository;
import com.example.epcot_thymeleaf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

  public Page<BoardEntity> getMyBoardsPage(Pageable pageable) {

    return boardRepository.findAll(pageable);
  }

  @Transactional
  public void editUserInfo(String currentLoginIdValue, UserEditDTO userEditDTO) {
    UserEntity user = userRepository.findByUsername(currentLoginIdValue)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

    if (!userEditDTO.getPassword().equals(userEditDTO.getPasswordCheck())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
    }

    if (userEditDTO.getPassword() != null && !userEditDTO.getPassword().isBlank()) {
      user.setPassword(passwordEncoder.encode(userEditDTO.getPassword()));
    }

  }
}
