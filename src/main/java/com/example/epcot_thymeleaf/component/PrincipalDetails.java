//package com.example.epcot_thymeleaf.component;
//
//import com.example.epcot_thymeleaf.entity.UserEntity;
//import lombok.Getter;
//import org.jspecify.annotations.Nullable;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.ArrayList;
//import java.util.Collection;
//
//
//@Getter
//public class PrincipalDetails implements UserDetails {
//
//  private UserEntity user;
//
//  public PrincipalDetails(UserEntity user) {
//    this.user = user;
//  }
//
//  @Override
//  public Collection<? extends GrantedAuthority> getAuthorities() {
//    Collection<GrantedAuthority> authorities = new ArrayList<>();
//    // 시큐리티 권한 체크 시 "ROLE_" 접두사를 기대하는 경우가 많으므로 확인 필요
//    authorities.add(() -> user.getRole().getRoleName());
//    return authorities;
//  }
//
//  @Override
//  public String getPassword() {
//    return user.getPassword();
//  }
//
//  @Override
//  public String getUsername() {
//    return user.getUsername();
//  }
//
//  // 💡 아래 메서드들을 모두 true로 명시적 변경합니다.
//  @Override
//  public boolean isAccountNonExpired() {
//    return true;
//  }
//
//  @Override
//  public boolean isAccountNonLocked() {
//    return true;
//  }
//
//  @Override
//  public boolean isCredentialsNonExpired() {
//    return true;
//  }
//
//  @Override
//  public boolean isEnabled() {
//    // 1. null 체크를 먼저 해주는 것이 안전합니다.
//    if (user.getStatus() == null) {
//      return true;
//    }
//
//    // 2. 객체가 아닌, 객체 내부의 '문자열 필드'를 가져와서 비교합니다.
//    // UserStatusEntity에 있는 필드명이 statusName이라고 가정합니다.
//    String currentStatus = user.getStatus().getStatusName();
//
//    // 3. DB 이미지에 있는 실제 값(suspended, withdrawal_req 등)과 비교합니다.
//    if ("suspended".equals(currentStatus) || "withdrawn".equals(currentStatus)) {
//      return false; // 로그인을 막음
//    }
//
//    return true; // 그 외(active, stage_1_approved 등)는 로그인 허용
//  }
//}