package com.example.epcot_thymeleaf.repository;

import com.example.epcot_thymeleaf.entity.BoardFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardFileRepository extends JpaRepository<BoardFileEntity, Long> {

  List<BoardFileEntity> findAllByBoardIdOrderByIdDesc(Long boardId);
}
