package com.example.epcot_thymeleaf.config;

import com.example.epcot_thymeleaf.component.UserStatusInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private String connectPath = "/images/**";
  private String resourcePath = "files:///C:/upload";
  private final UserStatusInterceptor userStatusInterceptor;

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addRedirectViewController("/", "/board/list");
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
    resolver.setOneIndexedParameters(true);
    resolvers.add(resolver);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler(connectPath)
      .addResourceLocations(resourcePath);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(userStatusInterceptor)
      .addPathPatterns("/**")               // 모든 경로에 대해 체크
      .excludePathPatterns(
          "/withdrawn-info",                // 안내 페이지는 제외
          "/login",
          "/logout",                        // 로그아웃은 할 수 있어야 함
          "/css/**", "/js/**", "/images/**" // 정적 리소스 제외
      );
  }

}
