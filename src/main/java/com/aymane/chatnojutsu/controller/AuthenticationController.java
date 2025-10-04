package com.aymane.chatnojutsu.controller;

import com.aymane.chatnojutsu.dto.LoginRequest;
import com.aymane.chatnojutsu.model.User;
import com.aymane.chatnojutsu.service.JwtService;
import com.aymane.chatnojutsu.service.CsrfService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    // 30 minutes
    @Value("${jwt.cookie.expiry-seconds:1800}")
    private int cookieExpiry;
    @Value("${jwt.cookie.expired-seconds:0}")
    private int cookieExpired;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                  JwtService jwtService,
                                  CsrfService csrfService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.csrfService = csrfService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
        Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);
        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);

        Object principal = authenticationResponse.getPrincipal();
        User user = (User) principal;

        String jwt = this.jwtService.createToken(user.getId());
        ResponseCookie jwtCookie = ResponseCookie.from("accessToken", jwt)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(cookieExpiry)
                .build();

        String csrfToken = this.csrfService.generateCsrfToken();
        ResponseCookie csrfCookie = csrfService.createCsrfCookie(csrfToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, csrfCookie.toString())
                .build();
    }



    @PostMapping("/logout")
    public ResponseEntity<?> unauthenticateUser(){
        // Create expired JWT cookie
        ResponseCookie jwtCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(cookieExpired)
                .build();

        // Create expired CSRF cookie
        ResponseCookie csrfCookie = csrfService.createExpiredCsrfCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, csrfCookie.toString())
                .build();
    }
}
