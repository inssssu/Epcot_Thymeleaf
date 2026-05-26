package com.example.epcot_thymeleaf.controller;

import com.example.epcot_thymeleaf.annotation.AdminAction;
import com.example.epcot_thymeleaf.entity.UserEntity;
import com.example.epcot_thymeleaf.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('admin')")
public class AdminController {

  private final UserService userService;

  // 사용자 전체 목록 조회
  @GetMapping("/users")
  public String userManagementPage(Model model, HttpSession session, Authentication authentication) {

    String loginId = authentication.getName();


    log.info("loginId >>> " + loginId);
//    if (loginUser == null || loginUser.getRole() != UserRole.ADMIN) {
//      return "redirect:/";
//    }

    model.addAttribute("userList", userService.findAllUsers());

    // 로그 확인용

    return "/admin/user/list";
  }

  // 사용자 탈퇴 승인 처리
  @PostMapping("/user/approve-withdrawal")
  @AdminAction(action = "회원 탈퇴 승인", details = "사용자 탈퇴 승인 처리", menu = "사용자 관리")
  public String approveWithdrawal(@RequestParam Long userId, String adminRoleName, HttpSession session) {
    userService.completeWithdrawal(userId, adminRoleName);
    return "redirect:/admin/users";
  }

  // 사용자 탈퇴 반려 처리
  @PostMapping("/user/reject-withdrawal")
//  @AdminAction(action = "회원 탈퇴 반려", details = "사용자 탈퇴 반려 처리")
  public String rejectWithdrawal(@RequestParam Long userId, @RequestParam("reason") String reason) {

    System.out.println("전달된 사유 : " + reason);
    userService.rejectWithdrawal(userId, reason);

    return "redirect:/admin/users";
  }

  // 계정 정지 처리
  @PostMapping("/user/suspend")
  @AdminAction(action = "회원 정지", details = "사용자 계정 강제 정지 처리", menu = "사용자 관리")
  public String suspendUser(@RequestParam("userId") Long userId) {
    userService.suspendUser(userId);
    return "redirect:/admin/users";
  }


}



















