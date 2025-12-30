package com.example.epcot_thymeleaf.repository;

import com.example.epcot_thymeleaf.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

  List<BoardEntity> findByOrderByCreatedAtDesc();
}
