package com.example.epcot_thymeleaf.controller;

import com.example.epcot_thymeleaf.entity.BoardEntity;
import com.example.epcot_thymeleaf.entity.UserEntity;
import com.example.epcot_thymeleaf.service.BoardService;
import com.example.epcot_thymeleaf.service.MypageService;
import com.example.epcot_thymeleaf.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthController {

  private final MypageService mypageService;
  private final UserService userService;
  private final BoardService boardService;

  @GetMapping("/login")
  public String login(Authentication authentication, Model model) {

    if (authentication != null
      && authentication.isAuthenticated()
      && !(authentication instanceof AnonymousAuthenticationToken)
    ) {
      System.out.println("Username is not null : " + authentication.getName());
      model.addAttribute("loginId", authentication.getName());

      return "redirect:/";
      // 로그인 후 url 을 통해 다시 로그인 페이지로 이동 시 해당 페이지가 막히지 않는 이유는 세션관리가 되고있지 않기 때문임
      // 세션관리를 통해 로그인페이지 이동 제한을 걸어두면 해결될 듯
    }

    return "auth/login";
  }

//  @GetMapping("/")
//  public String index() {
//
//    return "index";
//  }

  @GetMapping("/mypage")
  public String mypage(
      // WebConfig 의 setOneIndexParameter 를 사용하기 위해선 @PageableDefault 를 사용해야 적용시킬 수 있음
      @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
      Authentication authentication,
      Model model) {

    String loginId = authentication.getName();

    UserEntity user = mypageService.getUserByLoginId(loginId);

    Page<BoardEntity> myBoardsPage = mypageService.getMyBoardsPage(pageable);
    Page<BoardEntity> boardPage = boardService.getBoardPage(pageable);
    List<BoardEntity> myBoards = mypageService.getMyBoards(user);

    System.out.println("myBoards : " + myBoards.size());

    model.addAttribute("user", user);
    model.addAttribute("myBoardsPage", myBoardsPage);
    model.addAttribute("myBoards", myBoards);
    model.addAttribute("page", boardPage.getNumber());

    return "auth/mypage";
  }
}
