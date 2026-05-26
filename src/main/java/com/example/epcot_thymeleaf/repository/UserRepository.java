package com.example.epcot_thymeleaf.repository;

import com.example.epcot_thymeleaf.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

  boolean existsByUsername(String username);

  @EntityGraph(attributePaths = {"role", "status"})
  Optional<UserEntity> findByUsername(String username);

  @EntityGraph(attributePaths = {"role", "status"})
  Optional<UserEntity> findById(Long id);

  List<UserEntity> findAllByOrderByUsernameAsc();

  List<UserEntity> findAllByOrderById();
}
