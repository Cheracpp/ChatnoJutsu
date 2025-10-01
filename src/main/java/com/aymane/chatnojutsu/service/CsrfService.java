package com.aymane.chatnojutsu.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CsrfService {

    private static final String CSRF_TOKEN_COOKIE_NAME = "XSRF-TOKEN";
    private static final String CSRF_TOKEN_HEADER_NAME = "X-XSRF-TOKEN";
    private static final int TOKEN_LENGTH = 32;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generate a cryptographically secure random CSRF token
     */
    public String generateCsrfToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Create a CSRF token cookie (readable by JavaScript for header inclusion)
     */
    public ResponseCookie createCsrfCookie(String token) {
        return ResponseCookie.from(CSRF_TOKEN_COOKIE_NAME, token)
                .httpOnly(false)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(1800)     // 30 minutes (to match JWT expiry)
                .build();
    }

    /**
     * Extract CSRF token from cookie
     */
    public String getTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if (CSRF_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Extract CSRF token from header
     */
    public String getTokenFromHeader(HttpServletRequest request) {
        return request.getHeader(CSRF_TOKEN_HEADER_NAME);
    }

    /**
     * Validate that cookie and header tokens match (Double Submit Cookie pattern)
     */
    public boolean validateCsrfToken(HttpServletRequest request) {
        String cookieToken = getTokenFromCookie(request);
        String headerToken = getTokenFromHeader(request);

        // Both tokens must be present and match
        return cookieToken != null
               && headerToken != null
               && cookieToken.equals(headerToken);
    }

    /**
     * Create an expired CSRF cookie for logout
     */
    public ResponseCookie createExpiredCsrfCookie() {
        return ResponseCookie.from(CSRF_TOKEN_COOKIE_NAME, "")
                .httpOnly(false)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)  // Immediate expiry
                .build();
    }
}
