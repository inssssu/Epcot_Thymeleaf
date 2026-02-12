package com.example.epcot_thymeleaf.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEditDTO {

  private String username;
  private String password;
  private String passwordCheck;
}
