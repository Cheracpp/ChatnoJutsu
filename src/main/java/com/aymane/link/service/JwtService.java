package com.aymane.link.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtService {

  private final SecretKey key;

  // 30 minutes
  private final int cookieExpiry;

  public JwtService(@Value("${jwt.secret}") String secret,
      @Value("${jwt.cookie.expiry-seconds:1800}") int cookieExpiry) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.cookieExpiry = cookieExpiry;
  }


  public String createToken(String userId) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + cookieExpiry * 1000L);

    return Jwts.builder()
               .subject(userId)
               .issuedAt(new Date())
               .expiration(expiryDate)
               .signWith(key)
               .compact();
  }

  public String resolveTokenFromCookie(HttpServletRequest request) {
    String token = null;
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (cookie.getName()
                  .equals("accessToken")) {
          token = cookie.getValue();
        }
      }
      return token;
    }
    return null;
  }

  // Check if the token is valid and not expired
  public boolean validateToken(String token) {

    try {
      Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (MalformedJwtException ex) {
      log.error("Invalid JWT token");
    } catch (ExpiredJwtException ex) {
      log.error("Expired JWT token");
    } catch (UnsupportedJwtException ex) {
      log.error("the jwt argument does not represent a signed Claims JWT");
    } catch (IllegalArgumentException ex) {
      log.error("the jwt string is null or empty or only whitespace");
    } catch (SignatureException ex) {
      log.error("Invalid JWT signature");
    }
    return false;
  }

  // Extract the username from the JWT token
  public String getSubject(String token) {

    return Jwts.parser()
               .verifyWith(key)
               .build()
               .parseSignedClaims(token)
               .getPayload()
               .getSubject();
  }

  public ResponseCookie createJwtCookie(String token) {
    return ResponseCookie.from("accessToken", token)
                         .httpOnly(true)
                         .secure(true)
                         .sameSite("Lax")
                         .path("/")
                         .maxAge(cookieExpiry)
                         .build();
  }

  public ResponseCookie createExpiredJwtCookie() {
    return ResponseCookie.from("accessToken", "")
                         .httpOnly(true)
                         .secure(true)
                         .sameSite("Lax")
                         .path("/")
                         .maxAge(0)
                         .build();
  }
}
