package com.example.epcot_thymeleaf.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_role")
@NoArgsConstructor
public class UserRoleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "role_name", nullable = false, unique = true, length = 50)
  private String roleName;

  @Column(name = "description", length = 255)
  private String description;

  public UserRoleEntity(String roleName, String description) {
    this.roleName = roleName;
    this.description = description;
  }

}
