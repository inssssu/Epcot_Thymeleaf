package com.example.epcot_thymeleaf.repository;

import com.example.epcot_thymeleaf.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
  Optional<UserRoleEntity> findByRoleName(String roleName);
}
