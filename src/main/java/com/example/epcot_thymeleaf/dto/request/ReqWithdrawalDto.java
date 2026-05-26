package com.example.epcot_thymeleaf.dto.request;

import com.example.epcot_thymeleaf.entity.UserEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReqWithdrawalDto {

  private Long userId;
  private String username;
  private String action;
  private String status;
  private String withdrawalReason;
  private String adminMemo;
  private LocalDateTime withdrawalRequestedAt;

}
