package com.example.epcot_thymeleaf.repository;

import com.example.epcot_thymeleaf.entity.UserStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStatusRepository extends JpaRepository<UserStatusEntity, Long> {
  Optional<UserStatusEntity> findByStatusName(String statusName);
}
