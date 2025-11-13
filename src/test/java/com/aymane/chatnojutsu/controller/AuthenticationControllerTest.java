package com.aymane.chatnojutsu.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aymane.chatnojutsu.config.ControllersSecurityConfig;
import com.aymane.chatnojutsu.config.CustomUserDetails;
import com.aymane.chatnojutsu.dto.LoginRequest;
import com.aymane.chatnojutsu.service.CsrfService;
import com.aymane.chatnojutsu.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = AuthenticationController.class)
@Import(ControllersSecurityConfig.class)
public class AuthenticationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private AuthenticationManager authenticationManager;

  @MockitoBean
  private CsrfService csrfService;

  @MockitoBean
  private JwtService jwtService;

  @Test
  public void authenticateUser_WithValidInputs_ReturnsOkAndResponseContainsCookies()
      throws Exception {
    LoginRequest loginRequest = new LoginRequest("username", "Password123@");

    Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
        loginRequest.username(), loginRequest.password());

    CustomUserDetails userDetails = new CustomUserDetails("1", "username", "Password123@",
        new ArrayList<>());

    Authentication authenticationResponse = new UsernamePasswordAuthenticationToken(userDetails,
        userDetails.getPassword(), userDetails.getAuthorities());

    String jwt = "header.payload.signature";
    String csrfToken = "CsrfToken";

    ResponseCookie jwtCookie = ResponseCookie.from("accessToken", jwt)
                                             .httpOnly(true)
                                             .secure(true)
                                             .sameSite("Lax")
                                             .path("/")
                                             .maxAge(1800)
                                             .build();
    ResponseCookie csrfCookie = ResponseCookie.from("XSRF-TOKEN", csrfToken)
                                              .httpOnly(false)
                                              .secure(true)
                                              .sameSite("Lax")
                                              .path("/")
                                              .maxAge(1800)
                                              .build();

    given(authenticationManager.authenticate(authenticationRequest)).willReturn(
        authenticationResponse);
    given(jwtService.createToken(userDetails.getUsername())).willReturn(jwt);
    given(jwtService.createJwtCookie(jwt)).willReturn(jwtCookie);
    given(csrfService.generateCsrfToken()).willReturn(csrfToken);
    given(csrfService.createCsrfCookie(csrfToken)).willReturn(csrfCookie);

    mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                                       .content(objectMapper.writeValueAsString(loginRequest)))
           .andExpect(status().isOk())
           .andExpect(cookie().exists("XSRF-TOKEN"))
           .andExpect(cookie().value("XSRF-TOKEN", csrfToken))
           .andExpect(cookie().httpOnly("XSRF-TOKEN", false))
           .andExpect(cookie().secure("XSRF-TOKEN", true))
           .andExpect(cookie().sameSite("XSRF-TOKEN", "Lax"))
           .andExpect(cookie().maxAge("XSRF-TOKEN", 1800))
           .andExpect(cookie().exists("accessToken"))
           .andExpect(cookie().value("accessToken", jwt))
           .andExpect(cookie().httpOnly("accessToken", true))
           .andExpect(cookie().secure("accessToken", true))
           .andExpect(cookie().sameSite("accessToken", "Lax"))
           .andExpect(cookie().maxAge("accessToken", 1800));

    verify(authenticationManager).authenticate(authenticationRequest);
    verify(jwtService).createToken(userDetails.getUsername());
    verify(csrfService).generateCsrfToken();
    verify(csrfService).createCsrfCookie(csrfToken);
  }


  @Test
  public void authenticateUser_WithBadCredentials_RedirectsToLoginPage() throws Exception {
    LoginRequest loginRequest = new LoginRequest("username", "Password123@");

    Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
        loginRequest.username(), loginRequest.password());

    given(authenticationManager.authenticate(authenticationRequest)).willThrow(
        BadCredentialsException.class);
    mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                                       .content(objectMapper.writeValueAsString(loginRequest)))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("http://localhost/login"))
           .andExpect(cookie().doesNotExist("XSRF-TOKEN"))
           .andExpect(cookie().doesNotExist("accessToken"));

    verify(authenticationManager).authenticate(authenticationRequest);
    verifyNoInteractions(jwtService);
    verifyNoInteractions(csrfService);
  }

  @ParameterizedTest
  @CsvSource({"password123@, Password must contain at least one uppercase letter",
      "Password123, Password must contain at least one special character",
      "Password@, Password must contain at least one digit"})
  public void authenticateUser_WithInvalidPassword_ReturnsBadRequest(String invalidPassword,
      String reason) throws Exception {

    LoginRequest loginRequest = new LoginRequest("username", invalidPassword);

    mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                                       .content(objectMapper.writeValueAsString(loginRequest)))
           .andExpect(status().isBadRequest())
           .andExpect(cookie().doesNotExist("XSRF-TOKEN"))
           .andExpect(cookie().doesNotExist("accessToken"));

    verifyNoInteractions(authenticationManager);
    verifyNoInteractions(jwtService);
    verifyNoInteractions(csrfService);
  }

  @Test
  @WithMockUser
  public void unauthenticateUser_WithAuthenticatedUser_ReturnsOkAndClearsAuthenticationCookies()
      throws Exception {
    ResponseCookie jwtCookie = ResponseCookie.from("accessToken", "")
                                             .httpOnly(true)
                                             .secure(true)
                                             .sameSite("Lax")
                                             .path("/")
                                             .maxAge(0)
                                             .build();

    ResponseCookie csrfCookie = ResponseCookie.from("XSRF-TOKEN", "")
                                              .httpOnly(false)
                                              .secure(true)
                                              .sameSite("Lax")
                                              .path("/")
                                              .maxAge(0)  // Immediate expiry
                                              .build();

    given(jwtService.createExpiredJwtCookie()).willReturn(jwtCookie);
    given(csrfService.createExpiredCsrfCookie()).willReturn(csrfCookie);

    mockMvc.perform(post("/auth/logout"))
           .andExpect(status().isOk())
           .andExpect(cookie().exists("XSRF-TOKEN"))
           .andExpect(cookie().value("XSRF-TOKEN", ""))
           .andExpect(cookie().httpOnly("XSRF-TOKEN", false))
           .andExpect(cookie().secure("XSRF-TOKEN", true))
           .andExpect(cookie().sameSite("XSRF-TOKEN", "Lax"))
           .andExpect(cookie().maxAge("XSRF-TOKEN", 0))
           .andExpect(cookie().exists("accessToken"))
           .andExpect(cookie().value("accessToken", ""))
           .andExpect(cookie().httpOnly("accessToken", true))
           .andExpect(cookie().secure("accessToken", true))
           .andExpect(cookie().sameSite("accessToken", "Lax"))
           .andExpect(cookie().maxAge("accessToken", 0));

    verify(jwtService).createExpiredJwtCookie();
    verify(csrfService).createExpiredCsrfCookie();
  }
}
