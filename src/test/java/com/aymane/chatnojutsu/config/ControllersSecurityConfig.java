package com.aymane.chatnojutsu.config;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@TestConfiguration
public class ControllersSecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

    httpSecurity.headers(
                    headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny))
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)  // Using custom CSRF filter
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                    authorizeRequests -> authorizeRequests.requestMatchers(HttpMethod.POST,
                                                              "/api/users", "/auth/login")
                                                          .permitAll()
                                                          .dispatcherTypeMatchers(FORWARD, ERROR)
                                                          .permitAll()
                                                          .anyRequest()
                                                          .authenticated())
                .sessionManagement(
                    session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(
                    new LoginUrlAuthenticationEntryPoint("/login")));

    return httpSecurity.build();
  }
}
