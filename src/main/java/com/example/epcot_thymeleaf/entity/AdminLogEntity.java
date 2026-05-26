package com.example.epcot_thymeleaf.entity;

import com.example.epcot_thymeleaf.dto.request.ReqAdminUserDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "admin_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminLogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "admin_id")
  private UserEntity admin;

  @ManyToOne
  @JoinColumn(name = "target_user_id")
  private UserEntity targetUser;

  private String action;

  @Column(columnDefinition = "TEXT")
  private String details;
  private String ipAddress;

  @Column(updatable = false)
  private LocalDateTime timestamp;

  @Builder
  public AdminLogEntity(UserEntity admin, UserEntity targetUser, String action, String details, String ipAddress) {
    this.admin = admin;
    this.targetUser = targetUser;
    this.action = action;
    this.details = details;
    this.ipAddress = ipAddress;
    this.timestamp = LocalDateTime.now();
  }
}
