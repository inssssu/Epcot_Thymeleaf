package com.example.epcot_thymeleaf.service;

import com.example.epcot_thymeleaf.entity.BoardEntity;
import com.example.epcot_thymeleaf.repository.BoardFileRepository;
import com.example.epcot_thymeleaf.repository.BoardRepository;
import com.example.epcot_thymeleaf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

  private final BoardRepository boardRepository;
  private final BoardFileRepository boardFileRepository;
  private final UserRepository userRepository;



  public List<BoardEntity> getBoardList() {

    return boardRepository.findByOrderByCreatedAtDesc();
  }

  public BoardEntity getBoardItem(Long id) {

    BoardEntity board = boardRepository.findById(id).orElse(null);

    assert board != null;
    board.setTitle(board.getTitle());
    board.setContent(board.getContent());

    return board;
  }

  public void write(BoardEntity board) {
    boardRepository.save(board);
  }

  public void delete(Long id) { boardRepository.deleteById(id); }

  public Page<BoardEntity> getBoardPage(Pageable pageable) {

    return boardRepository.findAll(pageable);
  }


}
