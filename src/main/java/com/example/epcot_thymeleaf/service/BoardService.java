package com.example.epcot_thymeleaf.service;

import com.example.epcot_thymeleaf.dto.response.BoardResponseDTO;
import com.example.epcot_thymeleaf.entity.BoardEntity;
import com.example.epcot_thymeleaf.entity.UserEntity;
import com.example.epcot_thymeleaf.repository.BoardRepository;
import com.example.epcot_thymeleaf.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

  private final BoardRepository boardRepository;
  private final UserRepository userRepository;

  public List<BoardEntity> getBoardList() {

    return boardRepository.findByOrderByCreatedAtDesc();
  }

  public Optional<BoardEntity> getBoardItem(Long id) {
    return boardRepository.findById(id);
  }

  public void write(BoardEntity board) {
    boardRepository.save(board);
  }
}
