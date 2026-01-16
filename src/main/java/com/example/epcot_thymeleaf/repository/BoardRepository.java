package com.example.epcot_thymeleaf.repository;

import com.example.epcot_thymeleaf.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

  List<BoardEntity> findByOrderByCreatedAtDesc();

  Page<BoardEntity> findAll(Pageable pageable);

  Page<BoardEntity> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);


}
