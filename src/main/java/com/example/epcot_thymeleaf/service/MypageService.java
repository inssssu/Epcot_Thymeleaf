package com.example.epcot_thymeleaf.service;

import com.example.epcot_thymeleaf.entity.BoardEntity;
import com.example.epcot_thymeleaf.entity.UserEntity;
import com.example.epcot_thymeleaf.repository.BoardRepository;
import com.example.epcot_thymeleaf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {

  private final UserRepository userRepository;
  private final BoardRepository boardRepository;

  public UserEntity getUserByLoginId(String loginId) {
    return userRepository.findByUsername(loginId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다." + loginId));
  }

  public List<BoardEntity> getMyBoards(Long userId) {
    return boardRepository.findAllByIdOrderById(userId);
  }

  public Page<BoardEntity> getMyBoardsPage(Long userId, int page) {

    Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

    return boardRepository.findByAuthor_IdOrderByCreatedAtDesc(userId, pageable);
  }
}
