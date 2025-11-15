package com.aymane.chatnojutsu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

  private static final String SECRET_KEY_STRING = "nmJh5iEMj4OCrhjXiAjb+ZJVvVcd0+EgaWDs7+lW1tdJN+N7upc4Lw14hpbJ8uJFRDuj/J4Ds/bxJ7WKF7ntAw==";
  private static final String TEST_USER_ID = "1";
  private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
      SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));
  private static final int COOKIE_EXPIRY = 1800;

  @Mock(stubOnly = true)
  HttpServletRequest request;

  private final JwtService jwtService;

  public JwtServiceTest() {
    this.jwtService = new JwtService(SECRET_KEY_STRING, COOKIE_EXPIRY);
  }

  @Test
  public void createToken_WithValidInput_ReturnsValidJwt() throws Exception {
    String token = jwtService.createToken(TEST_USER_ID);

    Jwts.parser()
        .verifyWith(SECRET_KEY)
        .build()
        .parseSignedClaims(token);
  }

  @Test
  public void resolveTokenFromCookie_WithRequestContainsCookie_ReturnsToken() throws Exception {
    String token = createValidToken();
    Cookie cookie = new Cookie("accessToken", token);

    given(request.getCookies()).willReturn(new Cookie[]{cookie});

    String resolvedToken = jwtService.resolveTokenFromCookie(request);

    assertThat(resolvedToken).isNotNull();
    assertThat(resolvedToken).isEqualTo(token);
  }

  @Test
  public void resolveTokenFromCookie_WithInvalidCookieName_ReturnsNull() throws Exception {
    String token = createValidToken();
    Cookie cookie = new Cookie("someName", token);

    given(request.getCookies()).willReturn(new Cookie[]{cookie});

    String resolvedToken = jwtService.resolveTokenFromCookie(request);

    assertThat(resolvedToken).isNull();
  }

  @Test
  public void validateToken_WithValidToken_ReturnsTrue() throws Exception {
    String token = createValidToken();

    boolean result = jwtService.validateToken(token);

    assertThat(result).isTrue();
  }

  @Test
  public void validateToken_WithInvalidSignature_ReturnsFalse() throws Exception {
    SecretKey wrongSecretKey = Keys.hmacShaKeyFor(
        "Y/WwUe7eAqc/dtqmmWHxO8RZ/QY3KJcw/sh+Jls5v9K3szIpRiwQbvCNx9IFDhwo1eHQjW70YsE8bhIYqKzy6g==".getBytes(
            StandardCharsets.UTF_8));
    Date expiryDate = new Date(new Date().getTime() + COOKIE_EXPIRY * 1000L);
    String token = createToken(TEST_USER_ID, expiryDate, wrongSecretKey);

    boolean result = jwtService.validateToken(token);

    assertThat(result).isFalse();
  }

  @Test
  public void validateToken_WithExpiredToken_ReturnsFalse() throws Exception {
    Date expiryDate = new Date(new Date().getTime() - 1000L); // now - 1 second
    String token = createToken(TEST_USER_ID, expiryDate, SECRET_KEY);

    boolean result = jwtService.validateToken(token);

    assertThat(result).isFalse();
  }

  @Test
  public void validateToken_WithNullToken_ReturnsFalse() throws Exception {
    boolean result = jwtService.validateToken(null);
    assertThat(result).isFalse();
  }

  @Test
  public void validateToken_WithBlankToken_ReturnsFalse() throws Exception {
    boolean result = jwtService.validateToken("");
    assertThat(result).isFalse();
  }

  @Test
  public void getSubject_WithValidToken_ReturnsSubject() throws Exception {
    String token = createValidToken();

    String subject = jwtService.getSubject(token);

    assertThat(subject).isNotNull();
    assertThat(subject).isEqualTo(TEST_USER_ID);
  }

  @Test
  public void createExpiredJwtCookie_ReturnsExpiredCookie() {
    ResponseCookie expiredCookie = jwtService.createExpiredJwtCookie();

    assertThat(expiredCookie.getName()).isEqualTo("accessToken");
    assertThat(expiredCookie.getValue()).isEmpty();
    assertThat(expiredCookie.isHttpOnly()).isTrue();
    assertThat(expiredCookie.isSecure()).isTrue();
    assertThat(expiredCookie.getSameSite()).isEqualTo("Lax");
    assertThat(expiredCookie.getPath()).isEqualTo("/");
    assertThat(expiredCookie.getMaxAge()
                            .getSeconds()).isEqualTo(0);
  }

  @Test
  public void createJwtCookie_WithValidToken_ReturnsCookie() {
    String token = createValidToken();

    ResponseCookie jwtCookie = jwtService.createJwtCookie(token);

    assertThat(jwtCookie.getValue()).isNotEmpty();
    assertThat(jwtCookie.getName()).isEqualTo("accessToken");
    assertThat(jwtCookie.isHttpOnly()).isTrue();
    assertThat(jwtCookie.isSecure()).isTrue();
    assertThat(jwtCookie.getSameSite()).isEqualTo("Lax");
    assertThat(jwtCookie.getPath()).isEqualTo("/");
    assertThat(jwtCookie.getMaxAge()
                        .getSeconds()).isGreaterThan(0);
  }

  private String createValidToken() {
    Date expiryDate = new Date(new Date().getTime() + COOKIE_EXPIRY * 1000L);
    return createToken(TEST_USER_ID, expiryDate, SECRET_KEY);
  }

  private String createToken(String userId, Date expiryDate, SecretKey secretKey) {
    return Jwts.builder()
               .subject(userId)
               .issuedAt(new Date())
               .expiration(expiryDate)
               .signWith(secretKey)
               .compact();
  }
}
