package com.example.epcot_thymeleaf.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("---------- configure ---------------");
        System.out.println("password encode");
        System.out.println(new BCryptPasswordEncoder().encode("1234"));

        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/login", "/board/list", "/error").permitAll()
                .anyRequest().authenticated()
        );

        http.formLogin((auth) -> auth
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/board/list", true)
                .failureUrl("/login?error")
                .permitAll()
        )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                );
//                .formLogin()        // success handler, fail handler 설정
        // sha-512 : 암호화만 되는 복호화 되지 않음.

        return http.build();
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }
}
