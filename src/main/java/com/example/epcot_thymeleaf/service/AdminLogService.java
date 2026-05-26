package com.example.epcot_thymeleaf.service;

import com.example.epcot_thymeleaf.dto.request.ReqAdminUserDto;
import com.example.epcot_thymeleaf.entity.AdminLogEntity;
import com.example.epcot_thymeleaf.entity.UserEntity;
import com.example.epcot_thymeleaf.repository.AdminLogRepository;
import com.example.epcot_thymeleaf.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminLogService {

  private final AdminLogRepository adminLogRepository;
  private final UserRepository userRepository;

  // 로그 저장
  @Transactional
  public void saveLog(ReqAdminUserDto adminUserDto, UserEntity target, String action, String details, String ip) {
    UserEntity adminEntity = userRepository.getReferenceById(adminUserDto.getId());

    AdminLogEntity log = AdminLogEntity.builder()
        .admin(adminEntity)
        .targetUser(target)
        .action(action)
        .details(details)
        .ipAddress(ip)
        .build();

    adminLogRepository.save(log);
  }

  public List<AdminLogEntity> getAllLogs() {
    return adminLogRepository.findByOrderByTimestampDesc();
  }

  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if (ip == null) ip = request.getRemoteAddr();
    return ip;
  }

  public Page<AdminLogEntity> getLogsByUserRole(UserEntity user, Pageable pageable) {
    String roleName = user.getRole().getRoleName();

    if ("ADMIN".equals(roleName) || "MASTER_ADMIN".equals(roleName)) {
      return adminLogRepository.findByAdmin(user, pageable);
    }

    return adminLogRepository.findByTargetUser(user, pageable);
  }

  public Page<AdminLogEntity> getLogList(Pageable pageable) {


    return adminLogRepository.findByOrderByTimestampDesc(pageable);
  }
}
