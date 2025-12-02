package com.aymane.link.controller;

import com.aymane.link.config.CustomUserDetails;
import com.aymane.link.dto.LoginRequest;
import com.aymane.link.service.CsrfService;
import com.aymane.link.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final CsrfService csrfService;

  @Autowired
  public AuthenticationController(AuthenticationManager authenticationManager,
      JwtService jwtService, CsrfService csrfService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.csrfService = csrfService;
  }

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
        loginRequest.username(), loginRequest.password());
    Authentication authenticationResponse = this.authenticationManager.authenticate(
        authenticationRequest);
    SecurityContextHolder.getContext()
                         .setAuthentication(authenticationResponse);

    Object principal = authenticationResponse.getPrincipal();
    CustomUserDetails userDetails = (CustomUserDetails) principal;

    String jwt = this.jwtService.createToken(userDetails.getUsername());
    ResponseCookie jwtCookie = jwtService.createJwtCookie(jwt);

    String csrfToken = this.csrfService.generateCsrfToken();
    ResponseCookie csrfCookie = csrfService.createCsrfCookie(csrfToken);

    return ResponseEntity.ok()
                         .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                         .header(HttpHeaders.SET_COOKIE, csrfCookie.toString())
                         .build();
  }


  @PostMapping("/logout")
  public ResponseEntity<?> unauthenticateUser() {
    // Create expired JWT cookie
    ResponseCookie jwtCookie = jwtService.createExpiredJwtCookie();

    // Create expired CSRF cookie
    ResponseCookie csrfCookie = csrfService.createExpiredCsrfCookie();

    return ResponseEntity.ok()
                         .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                         .header(HttpHeaders.SET_COOKIE, csrfCookie.toString())
                         .build();
  }
}
