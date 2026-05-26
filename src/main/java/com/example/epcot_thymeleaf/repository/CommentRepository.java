package com.example.epcot_thymeleaf.repository;

import com.example.epcot_thymeleaf.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

  List<CommentEntity> findByPostIdAndParentIsNullOrderByCreatedAtAsc(Long postId);
}
