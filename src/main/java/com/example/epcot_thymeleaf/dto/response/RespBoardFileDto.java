package com.example.epcot_thymeleaf.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RespBoardFileDto {

  private Long id;
  private Long boardId;
  private String originalName;
  private String storedName;
  private String storedPath;
  private String contentType;
  private long size;
  private LocalDateTime createdAt;
}
