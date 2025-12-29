package com.example.epcot_thymeleaf.repository;

import com.example.epcot_thymeleaf.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {


}
