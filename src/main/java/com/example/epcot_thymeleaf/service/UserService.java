package com.example.epcot_thymeleaf.service;

import com.example.epcot_thymeleaf.dto.request.UserRequestDTO;
import com.example.epcot_thymeleaf.entity.UserEntity;
import com.example.epcot_thymeleaf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity u = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found : " + username));

        return User.builder()
                .username(u.getUsername())
                .password(u.getPassword())
                .build();
    }

    @Transactional
    public String join(UserRequestDTO dto) {
      if (userRepository.existsByUsername(dto.getUsername())) {
        throw new RuntimeException("이미 존재하는 아이디입니다");
      }

      UserEntity user = UserEntity.builder()
          .username(dto.getUsername())
          .password(passwordEncoder.encode(dto.getPassword()))
          .build();

      UserEntity savedUser = userRepository.save(user);

      System.out.println("user Id : " + savedUser.getId());

      return "success";
    }
}
