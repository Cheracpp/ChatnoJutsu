package com.aymane.chatnojutsu.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtService {

  SecretKey key = Jwts.SIG.HS512.key()
                                .build();

  // 30 minutes
  @Value("${jwt.cookie.expiry-seconds:1800}")
  private int cookieExpiry;

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
}
