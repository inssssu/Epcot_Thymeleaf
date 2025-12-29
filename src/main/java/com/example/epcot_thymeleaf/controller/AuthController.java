package com.example.epcot_thymeleaf.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(Authentication authentication) {

        if (authentication != null
            && authentication.isAuthenticated()
        ) {
            return "redirect:/";
            // 로그인 후 url 을 통해 다시 로그인 페이지로 이동 시 해당 페이지가 막히지 않는 이유는 세션관리가 되고있지 않기 때문임
            // 세션관리를 통해 로그인페이지 이동 제한을 걸어두면 해결될 듯
        }

        return "auth/login";
    }

    @GetMapping("/")
    public String index() {

        return "index";
    }
}
