package com.example.epcot_thymeleaf.aspect;

import com.example.epcot_thymeleaf.annotation.AdminAction;
import com.example.epcot_thymeleaf.entity.AdminLogEntity;
import com.example.epcot_thymeleaf.entity.UserEntity;
import com.example.epcot_thymeleaf.repository.AdminLogRepository;
import com.example.epcot_thymeleaf.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LogAspect {

  private final AdminLogRepository adminLogRepository;
  private final HttpServletRequest request;
  private final UserRepository userRepository;

  @Around("@annotation(adminAction)")
  public Object logAdminActivity(ProceedingJoinPoint joinPoint, AdminAction adminAction) throws Throwable {

    Object result = joinPoint.proceed();

    try {
      String currentAdminId = SecurityContextHolder.getContext().getAuthentication().getName();

      UserEntity admin = userRepository.findByUsername(currentAdminId)
          .orElseThrow(() -> new RuntimeException("관리자 정보를 찾을 수 없습니다."));

      String methodName = joinPoint.getSignature().getName();
      String params = Arrays.toString(joinPoint.getArgs());
      String details = String.format("Method : %s, Args : %s", methodName, params);
      String ip = request.getRemoteAddr();

      log.info("[ADMIN_LOG] 관리자 : {}, 메뉴 : {}, 동작 : {}, 상세 : {}, IP : {}", admin, adminAction.action(), details, request.getRemoteAddr(), ip);

      AdminLogEntity adminLog = AdminLogEntity.builder()
          .admin(admin)
          .action(adminAction.action())
          .details(adminAction.details())
          .ipAddress(request.getRemoteAddr())
          .build();

      adminLogRepository.save(adminLog);
    } catch (Exception e) {
      log.error("관리자 행동 로그 저장 중 오류 발생 : " + e);
    }

    return result;
  }
}
















