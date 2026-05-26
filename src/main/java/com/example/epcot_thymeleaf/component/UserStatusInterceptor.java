package com.example.epcot_thymeleaf.component;

import com.example.epcot_thymeleaf.entity.UserEntity;
import com.example.epcot_thymeleaf.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class UserStatusInterceptor implements HandlerInterceptor {

  private final UserRepository userRepository;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    // 컨트롤러에 매핑된 요청이 아니면(정적 리소스 등) 무시한다
    if (!(handler instanceof HandlerMethod)) {
      return true;
    }

    // 현재 로그인한 사용자의 인증 정보 가져오기
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    // 로그인 상태이고, 익명 사용자가 아닐 때만 체크
    if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
      String username = auth.getName();
      UserEntity user = userRepository.findByUsername(username).orElse(null);

      // 상태가 stage_3_final 탈퇴 상태인 경우
      if (user != null && user.getStatus() != null && "stage_3_final".equals(user.getStatus().getStatusName())) {
        // 탈퇴한 사용자 안내 페이지로 이동
        response.sendRedirect("/withdrawn/info");

        // 더 이상 컨트롤러로 요청을 보내지 않고 여기서 끝냄
        return false;
      }

//      if (user != null && !user.isWithdrawalResultShown()) {
//        // 탈퇴 반려 사유가 있는지
//        if (user.getWithdrawalRejectionReason() != null) {
//          request.getSession().setAttribute("rejectionMessage", user.getWithdrawalRejectionReason());
//
//          user.setWithdrawalRejectionReason(null);
//          userRepository.save(user);
//        }
//      }

//      if (user != null && !user.isWithdrawalResultShown()) {
//        String message = (user.getStatus() == UserStatus.WITHDRAWN)
//            ? "회원 탈퇴가 승인되었습니다."
//            : "탈퇴 신청이 반려되었습니다. 사유 : " + user.getWithdrawalRejectionReason();
//
//        request.getSession().setAttribute("userNotice", message);
//
//        user.setWithdrawalResultShown(true);
//        userRepository.save(user);
//      }

      if (user != null ) {
        // 알람을 아직 확인하지 않은 경우
//        if (!user.isWithdrawalResultShown()) {
//          String reason = user.getWithdrawalRejectionReason();
//          if (reason == null || reason.isEmpty()) {
//            reason = user.getWithdrawalRejectionReason();
//          }
//
//          System.out.println("인터셉터 진입 - DB 사유 : " + user.getWithdrawalRejectionReason());
//
//          String message = (user.getStatus() == UserStatus.WITHDRAWN)
//              ? "탈퇴승인" : "탈퇴 반려 : " + reason;
//
//          request.getSession().setAttribute("userNotice", message);
//          System.out.println("세션저장 직후 : " + request.getSession().getAttribute("userNotice"));
//
//          user.setWithdrawalResultShown(true);
//          userRepository.save(user);
//        } else {
//          request.getSession().removeAttribute("userNotice");
//        }
        if (!user.isWithdrawalResultShown()) {
          String reason = user.getWithdrawalRejectionReason();
          if (reason == null || reason.trim().isEmpty()) {
            reason = "기재된 사유가 없습니다";
          }

          System.out.println("인터셉터 진입 - DB 사유 : " + reason);

          boolean isWithdrawn = user.getStatus() != null && "stage_3_final".equals(user.getStatus().getStatusName());

          String message = isWithdrawn
              ? "탈퇴 승인 사유 : " + reason
              : "탈퇴 반려 사유 : " + reason;

          request.getSession().setAttribute("userNotice", message);
          System.out.println("세션 저장 완료 : " + message);

          user.setWithdrawalResultShown(true);
          userRepository.save(user);

          System.out.println("알림 세션 저장 완료 : " + message);
        }
      }

      // 관리자가 정지시킨 사용자 계정인 경우
      if (user != null && user.getStatus() != null && "stage_3_final".equals(user.getStatus().getStatusName())) {
        response.sendRedirect("/withdrawn-info");

        return false;
      }

      // 관리자 페이지 권한 체크
      String uri = request.getRequestURI();

      // 현재 사용자의 권한 이름을 null 이 들어오지 못하게 방지
      String roleName = (user != null && user.getRole() != null) ? user.getRole().getRoleName() : "";

      // /admin 으로 시작하는 주소에 접근했을 때 권한 검사.
      // 권한이 admin, master_admin 이 아닐 때 접근 차단`
      if (uri.startsWith("/admin") && !("admin".equals(roleName) || "master_admin".equals(roleName))) {
        // 권한이 없는 사용자가 접근했을 경우
        response.sendRedirect("/board/list?error=denied");
        return false;
      }
    }

    // 정상적인 경우 컨트롤러를 실행
    return true;
  }
}
