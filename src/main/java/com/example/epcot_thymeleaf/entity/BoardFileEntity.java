package com.example.epcot_thymeleaf.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "board_file")
@Getter
@Setter
public class BoardFileEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long boardId;

  @Column(nullable = false)
  private String originalName;

  @Column(nullable = false)
  private String storedName;

  @Column(nullable = false)
  private String storedPath;

  private String contentType;
  private long size;
}
