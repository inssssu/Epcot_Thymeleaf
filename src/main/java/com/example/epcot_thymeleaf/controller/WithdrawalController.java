package com.example.epcot_thymeleaf.controller;

//import com.example.epcot_thymeleaf.component.PrincipalDetails;
import com.example.epcot_thymeleaf.dto.request.ReqAdminUserDto;
import com.example.epcot_thymeleaf.entity.UserEntity;
import com.example.epcot_thymeleaf.repository.UserRepository;
import com.example.epcot_thymeleaf.security.PrincipalUser;
import com.example.epcot_thymeleaf.service.UserService;
import com.example.epcot_thymeleaf.service.WithdrawalService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WithdrawalController {

  private final UserService userService;
  private final UserRepository userRepository;
  private final WithdrawalService withdrawalService;

  @GetMapping("/withdrawn/info")
  public String withdrawnInfo() {
    return "auth/withdrawn";
  }

  @GetMapping("/admin/withdrawal/first/{userId}")
  @PreAuthorize("hasAnyAuthority('admin', 'master_admin')")
  public String withdrawalFirstStep(@PathVariable Long userId, Model model) {

    UserEntity user = userService.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다"));
    model.addAttribute("user", user);

    log.info("사용자 ID 확인 : {}", userId);
    return "admin/withdrawal/first";
  }

  @GetMapping("/admin/withdrawal/second/{userId}")
  @PreAuthorize("hasAnyAuthority('admin', 'master_admin')")
  public String withdrawalSecondStep(@PathVariable Long userId,
                                     @ModelAttribute("adminMemo") String adminMemo,
                                     @ModelAttribute("withdrawalReason") String withdrawalReason,
                                     Model model) {

    UserEntity user = userService.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다"));

    model.addAttribute("user", user);
    model.addAttribute("adminMemo", adminMemo);
    model.addAttribute("withdrawalReason", withdrawalReason);

    log.info("userId >>> {}", userId);

    return "admin/withdrawal/second";
  }

  @GetMapping("/admin/withdrawal/final/{userId}")
  public String withdrawalFinalStep(@PathVariable Long userId,
                                    @ModelAttribute("adminMemo") String adminMemo,
                                    @ModelAttribute("withdrawalReason") String withdrawalReason,
                                    Model model) {

    log.info("사용자 ID 확인 : {}", userId);
    UserEntity user = userService.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다"));

    model.addAttribute("user", user);
    model.addAttribute("adminMemo", adminMemo);
    model.addAttribute("withdrawalReason", withdrawalReason);


    return "admin/withdrawal/final";
  }

//  @PostMapping("/admin/withdrawal/first/process")
//  public String firstProcess(@RequestParam Long userId,
//                             @RequestParam String adminMemo,
//                             @RequestParam String action,
//                             @AuthenticationPrincipal PrincipalDetails adminDetails,
//                             HttpServletRequest request) {
//

  /// /    userService.moveToSecondProcess(userId, adminMemo);
//
//    UserEntity admin = adminDetails.getUser();
//    String ip = request.getRemoteAddr();
//
//    if ("reject".equals(action)) {
//      withdrawalService.rejectWithdrawal(userId, admin, adminMemo, ip);
//      return "redirect:/withdrawal/list?msg=rejected";
//    } else {
//      withdrawalService.rejectWithdrawal(userId, admin, adminMemo, ip);
//      return "redirect:/admin/withdrawal/second/" + userId;
//    }
//  }

  // 회원탈퇴 첫번째 검토
  @PostMapping("/admin/withdrawal/first/process")
  public String firstProcess(@RequestParam("userId") Long userId,
                             @RequestParam("adminMemo") String adminMemo,
                             @RequestParam("withdrawalReason") String withdrawalReason,
                             ReqAdminUserDto adminUserDto,
                             @AuthenticationPrincipal UserDetails loginAdmin,
                             String ip,
                             RedirectAttributes reAttr) {

    log.info("User Details 의 정보 >>> {}", loginAdmin);

    adminInfo(adminUserDto, loginAdmin);

    log.info("AdminUserDto 의 id 정보 >>> {}", adminUserDto.getId());
    log.info("AdminUserDto 의 username 정보 >>> {}", adminUserDto.getUsername());

    // 2단계 전달할 내용 adminMemo, action, withdrawalReason, userId
    reAttr.addAttribute("adminMemo", adminMemo);
    reAttr.addAttribute("withdrawalReason", withdrawalReason);
    reAttr.addAttribute("userId", userId);

    withdrawalService.updateFirstAdminMemo(userId, adminMemo, adminUserDto, ip);

    return "redirect:/admin/withdrawal/second/" + userId;
  }

  // 회원탈퇴 두번째 검토
  @PostMapping("/admin/withdrawal/second/approve")
  public String secondProcess(@RequestParam("userId") Long userId,
                              @RequestParam("adminMemo") String adminMemo,
                              @RequestParam("withdrawalReason") String withdrawalReason,
                              ReqAdminUserDto adminUserDto,
                              @AuthenticationPrincipal UserDetails loginAdmin,
                              String ip,
                              RedirectAttributes reAttr) {

    adminInfo(adminUserDto, loginAdmin);

    reAttr.addAttribute("adminMemo", adminMemo);
    reAttr.addAttribute("withdrawalReason", withdrawalReason);
    reAttr.addAttribute("userId", userId);

    withdrawalService.updateSecondAdminMemo(userId, adminMemo, adminUserDto, ip);

    log.info("사용자 ID 확인 : {} ", userId);

    return "redirect:/admin/withdrawal/final/" + userId;
  }



  // 회원탈퇴 최종 검토
  @PostMapping("/admin/withdrawal/final/approve")
  public String finalProcess(@RequestParam("userId") Long userId,
                             @RequestParam("adminMemo") String adminMemo,
                             @RequestParam("withdrawalReason") String withdrawalReason,
                             ReqAdminUserDto adminUserDto,
                             @AuthenticationPrincipal UserDetails loginAdmin,
                             String ip,
                             RedirectAttributes reAttr) {

    adminInfo(adminUserDto, loginAdmin);

    reAttr.addAttribute("adminMemo", adminMemo);
    reAttr.addAttribute("withdrawalReason", withdrawalReason);
    reAttr.addAttribute("userId", userId);

    try {
      withdrawalService.approveWithdrawal(userId, adminMemo, adminUserDto, ip);

      reAttr.addAttribute("message", "사용자 탈퇴 처리가 최종 완료되었습니다");

      // 탈퇴 승인한 관리자의 정보도 넘겨줘야 함
    } catch (Exception e) {
      e.printStackTrace(); // 서버 콘솔창에 구체적인 에러 원인(Stack Trace)을 출력합니다.
      reAttr.addFlashAttribute("error", "오류 내용: " + e.getMessage());
      return "redirect:/admin/withdrawal/second/" + userId;
    }

    return "redirect:/admin/users";
  }

  // 회원탈퇴 반려
  @PostMapping("/admin/withdrawal/reject")
  public String rejectWithdrawal(@RequestParam("userId") Long userId,
                                 @RequestParam("rejectReason") String rejectReason,
                                 @RequestParam("adminMemo") String adminMemo,
                                 ReqAdminUserDto adminUserDto,
                                 @AuthenticationPrincipal UserDetails loginAdmin,
                                 String ip,
                                 RedirectAttributes reAttr) {

    adminInfo(adminUserDto, loginAdmin);

    try {
      withdrawalService.rejectWithdrawal(userId, adminUserDto, adminMemo, ip);

      reAttr.addFlashAttribute("message", "사용자 탈퇴 반려 처리가 완료되었습니다.");
    } catch (Exception e) {
      e.printStackTrace();
      reAttr.addFlashAttribute("error", "오류 내용 : " + e.getMessage());
      return "redirect:/admin/withdrawal/first/" + userId;
    }

    return "redirect:/admin/users";
  }

  private void adminInfo(ReqAdminUserDto adminUserDto, @AuthenticationPrincipal UserDetails loginAdmin) {
    UserEntity adminEntity = userRepository.findByUsername(loginAdmin.getUsername()
    ).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자 계정입니다"));

    adminUserDto.setId(adminEntity.getId());
    adminUserDto.setUsername(loginAdmin.getUsername());

    String role = loginAdmin.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .findFirst()
        .orElse(null);

    adminUserDto.setRole(role);
  }
}

























