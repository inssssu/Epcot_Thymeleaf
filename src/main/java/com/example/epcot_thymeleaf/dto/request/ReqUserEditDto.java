package com.example.epcot_thymeleaf.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUserEditDto {

  private String username;
  private String newPassword;
  private String newPasswordCheck;
}
