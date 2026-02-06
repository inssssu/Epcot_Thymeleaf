package com.example.epcot_thymeleaf.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BoardResponseDTO {

  private Long id;
  private String title;
  private String content;
  private String username;
  private LocalDateTime createdAt;


}
