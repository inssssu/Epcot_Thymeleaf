package com.example.epcot_thymeleaf.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String username;
  private String password;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "role_id")
  private UserRoleEntity role;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "status_id")
  private UserStatusEntity status;

  // 탈퇴 요청한 시점 기록
  @Column(name = "withdrawal_requested_at")
  private LocalDateTime withdrawalRequestedAt;

  // 탈퇴 사유
  private String withdrawalReason;

  private String withdrawalRejectionReason;
  private boolean withdrawalResultShown;

  @Column(name = "first_admin_memo")
  private String firstAdminMemo;
  @Column(name = "second_admin_memo")
  private String secondAdminMemo;

}













