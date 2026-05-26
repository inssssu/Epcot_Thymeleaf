package com.example.epcot_thymeleaf.controller;

//import com.example.epcot_thymeleaf.component.PrincipalDetails;
import com.example.epcot_thymeleaf.entity.AdminLogEntity;
import com.example.epcot_thymeleaf.entity.UserEntity;
import com.example.epcot_thymeleaf.service.AdminLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/admin/log")
@RequiredArgsConstructor
public class AdminLogController {

  private final AdminLogService adminLogService;

  @GetMapping("/list")
  public String listLogs(Model model,Pageable pageable) {

    Page<AdminLogEntity> logPage = adminLogService.getLogList(pageable);

    model.addAttribute("logs", logPage.getContent());
    model.addAttribute("page", logPage);

    log.info("logs : " + logPage);

    return "/admin/log/list";
  }

//  @GetMapping("/my-activity")
//  public String getMyLogs(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model, Pageable pageable) {
//
//    UserEntity currentUser = principalDetails.getUser();
//    Page<AdminLogEntity> logs = adminLogService.getLogsByUserRole(currentUser, pageable);
//
//    model.addAttribute("logs", logs);
//
//    return "admin/log/list";
//  }

}













