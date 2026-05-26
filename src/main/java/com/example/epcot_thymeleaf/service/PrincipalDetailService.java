//package com.example.epcot_thymeleaf.service;
//
////import com.example.epcot_thymeleaf.component.PrincipalDetails;
//import com.example.epcot_thymeleaf.entity.UserEntity;
//import com.example.epcot_thymeleaf.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class PrincipalDetailService implements UserDetailsService {
//
//  private final UserRepository userREpository;
//
//  @Override
//  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//
//    // DB 에 UserEntity 찾기
//    UserEntity userEntity = userREpository.findByUsername(username)
//        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
//
//    // 찾은 UserEntity 를 PrincipalDetails 라는 포장지로 감싸서 시큐리티에게 먼저 던져줌.
//    return new PrincipalDetails(userEntity);
//  }
//
//}
