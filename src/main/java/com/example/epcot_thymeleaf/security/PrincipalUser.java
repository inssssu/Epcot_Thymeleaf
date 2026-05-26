package com.example.epcot_thymeleaf.security;

import com.example.epcot_thymeleaf.entity.UserRoleEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
public class PrincipalUser implements UserDetails {

  private Long id;
  private String username;
  private String password;
  private Set<UserRoleEntity> userRoles;


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return userRoles.stream().map(
        userRoleEntity -> new SimpleGrantedAuthority(userRoleEntity.getRoleName())
    ).collect(Collectors.toSet());
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
