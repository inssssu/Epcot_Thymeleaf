package com.example.epcot_thymeleaf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminAction {

  // 메뉴 명
  String menu();
  // 회원 관리 등
  String details();
  // 탈퇴 승인, 반려 등
  String action();
  // 파라미터 정보 저장 여부
  boolean logArgs() default true;
}
