package com.aymane.link.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.aymane.link.exception.security.CsrfTokenException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;

@ExtendWith(MockitoExtension.class)
public class CsrfServiceTest {

  private final CsrfService csrfService;

  @Mock(stubOnly = true)
  HttpServletRequest request;

  public CsrfServiceTest() {
    csrfService = new CsrfService();
  }

  @Test
  public void generateCsrfToken_ReturnsRandomCsrfToken() {
    String token1 = csrfService.generateCsrfToken();
    String token2 = csrfService.generateCsrfToken();
    String token3 = csrfService.generateCsrfToken();

    assertThat(token1).isNotEmpty();
    assertThat(token2).isNotEmpty();
    assertThat(token3).isNotEmpty();

    assertThat(token1).isNotEqualTo(token2);
    assertThat(token1).isNotEqualTo(token3);
    assertThat(token3).isNotEqualTo(token2);
  }

  @Test
  public void createCsrfCookie_WithValidToken_ReturnsCookie() {
    String token1 = csrfService.generateCsrfToken();

    ResponseCookie csrfCookie = csrfService.createCsrfCookie(token1);

    assertThat(csrfCookie.getValue()).isNotEmpty();
    assertThat(csrfCookie.isHttpOnly()).isFalse();
    assertThat(csrfCookie.isSecure()).isTrue();
    assertThat(csrfCookie.getSameSite()).isEqualTo("Lax");
    assertThat(csrfCookie.getPath()).isEqualTo("/");
    assertThat(csrfCookie.getMaxAge()
                         .getSeconds()).isGreaterThan(0);
  }

  @ParameterizedTest
  @CsvSource(value = {"'', Token must not be Empty", "null, Token must not be Null"}, nullValues = {
      "null"})
  public void createCsrfCookie_WithInvalidToken_ThrowsCsrfTokenException(String invalidToken) {

    assertThatThrownBy(() -> csrfService.createCsrfCookie(invalidToken)).isInstanceOf(
                                                                            CsrfTokenException.class)
                                                                        .hasMessage(
                                                                            "Token cannot be null or blank");
  }

  @Test
  public void getTokenFromCookie_WithValidCookie_ReturnsToken() {
    String expectedToken = "test-csrf-token";
    Cookie csrfCookie = new Cookie("XSRF-TOKEN", expectedToken);
    when(request.getCookies()).thenReturn(new Cookie[]{csrfCookie});

    String token = csrfService.getTokenFromCookie(request);

    assertThat(token).isEqualTo(expectedToken);
  }

  @Test
  public void getTokenFromCookie_WithMultipleCookies_ReturnsCorrectToken() {
    String expectedToken = "test-csrf-token";
    Cookie cookie1 = new Cookie("SESSION", "session-value");
    Cookie csrfCookie = new Cookie("XSRF-TOKEN", expectedToken);
    Cookie cookie2 = new Cookie("OTHER", "other-value");
    when(request.getCookies()).thenReturn(new Cookie[]{cookie1, csrfCookie, cookie2});

    String token = csrfService.getTokenFromCookie(request);

    assertThat(token).isEqualTo(expectedToken);
  }

  @Test
  public void getTokenFromCookie_WithNoCookie_ReturnsNull() {
    when(request.getCookies()).thenReturn(new Cookie[]{});

    String token = csrfService.getTokenFromCookie(request);

    assertThat(token).isNull();
  }

  @Test
  public void getTokenFromCookie_WithNullCookies_ReturnsNull() {
    when(request.getCookies()).thenReturn(null);

    String token = csrfService.getTokenFromCookie(request);

    assertThat(token).isNull();
  }

  @Test
  public void getTokenFromHeader_WithValidHeader_ReturnsToken() {
    String expectedToken = "test-csrf-token";
    when(request.getHeader("X-XSRF-TOKEN")).thenReturn(expectedToken);

    String token = csrfService.getTokenFromHeader(request);

    assertThat(token).isEqualTo(expectedToken);
  }

  @Test
  public void getTokenFromHeader_WithNoHeader_ReturnsNull() {
    when(request.getHeader("X-XSRF-TOKEN")).thenReturn(null);

    String token = csrfService.getTokenFromHeader(request);

    assertThat(token).isNull();
  }

  @Test
  public void validateCsrfToken_WithMatchingTokens_ReturnsTrue() {
    String token = "test-csrf-token";
    Cookie csrfCookie = new Cookie("XSRF-TOKEN", token);
    when(request.getCookies()).thenReturn(new Cookie[]{csrfCookie});
    when(request.getHeader("X-XSRF-TOKEN")).thenReturn(token);

    boolean isValid = csrfService.validateCsrfToken(request);

    assertThat(isValid).isTrue();
  }

  @Test
  public void validateCsrfToken_WithMismatchedTokens_ReturnsFalse() {
    Cookie csrfCookie = new Cookie("XSRF-TOKEN", "cookie-token");
    when(request.getCookies()).thenReturn(new Cookie[]{csrfCookie});
    when(request.getHeader("X-XSRF-TOKEN")).thenReturn("header-token");

    boolean isValid = csrfService.validateCsrfToken(request);

    assertThat(isValid).isFalse();
  }

  @Test
  public void validateCsrfToken_WithMissingCookieToken_ReturnsFalse() {
    when(request.getCookies()).thenReturn(new Cookie[]{});
    when(request.getHeader("X-XSRF-TOKEN")).thenReturn("header-token");

    boolean isValid = csrfService.validateCsrfToken(request);

    assertThat(isValid).isFalse();
  }

  @Test
  public void validateCsrfToken_WithMissingHeaderToken_ReturnsFalse() {
    Cookie csrfCookie = new Cookie("XSRF-TOKEN", "cookie-token");
    when(request.getCookies()).thenReturn(new Cookie[]{csrfCookie});
    when(request.getHeader("X-XSRF-TOKEN")).thenReturn(null);

    boolean isValid = csrfService.validateCsrfToken(request);

    assertThat(isValid).isFalse();
  }

  @Test
  public void validateCsrfToken_WithBothTokensMissing_ReturnsFalse() {
    when(request.getCookies()).thenReturn(null);
    when(request.getHeader("X-XSRF-TOKEN")).thenReturn(null);

    boolean isValid = csrfService.validateCsrfToken(request);

    assertThat(isValid).isFalse();
  }

  @Test
  public void createExpiredCsrfCookie_ReturnsExpiredCookie() {
    ResponseCookie expiredCookie = csrfService.createExpiredCsrfCookie();

    assertThat(expiredCookie.getName()).isEqualTo("XSRF-TOKEN");
    assertThat(expiredCookie.getValue()).isEmpty();
    assertThat(expiredCookie.isHttpOnly()).isFalse();
    assertThat(expiredCookie.isSecure()).isTrue();
    assertThat(expiredCookie.getSameSite()).isEqualTo("Lax");
    assertThat(expiredCookie.getPath()).isEqualTo("/");
    assertThat(expiredCookie.getMaxAge()
                            .getSeconds()).isEqualTo(0);
  }

}
