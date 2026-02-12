package com.example.epcot_thymeleaf.repository;

import com.example.epcot_thymeleaf.entity.BoardEntity;
import com.example.epcot_thymeleaf.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

  List<BoardEntity> findByOrderByCreatedAtDesc();

  Page<BoardEntity> findAll(Pageable pageable);

  Page<BoardEntity> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

  @Query("select b from BoardEntity b where b.author.id = :userId order by b.createdAt desc")
  List<BoardEntity> findAllByIdOrderById(@Param("userId") Long userId);

  List<BoardEntity> findAllByAuthorOrderByIdDesc(UserEntity author);

}
