package com.example.epcot_thymeleaf.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqAdminUserDto {

  private Long id;
  private String username;
  private String role;

}
