package com.example.epcot_thymeleaf.repository;

import com.example.epcot_thymeleaf.entity.AdminLogEntity;
import com.example.epcot_thymeleaf.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminLogRepository extends JpaRepository<AdminLogEntity, Long> {

  Page<AdminLogEntity> findByAdmin(UserEntity admin, Pageable pageable);

  Page<AdminLogEntity> findByTargetUser(UserEntity targetUser, Pageable pageable);

  Page<AdminLogEntity> findByOrderByTimestampDesc(Pageable pageable);

  List<AdminLogEntity> findByAction(String action);

  List<AdminLogEntity> findByOrderByTimestampDesc();
}
