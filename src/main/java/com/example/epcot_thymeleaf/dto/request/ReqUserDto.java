package com.example.epcot_thymeleaf.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ReqUserDto {

  private String username;
  private String password;
  private String role;
}
