package com.aymane.chatnojutsu.config;

import com.aymane.chatnojutsu.service.CsrfService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@AllArgsConstructor
public class CsrfFilter extends OncePerRequestFilter {

  private final CsrfService csrfService;

  // HTTP methods that require CSRF protection
  private static final Set<String> PROTECTED_METHODS = Set.of("POST", "PUT", "DELETE", "PATCH");

  // Endpoints that don't require CSRF protection
  private static final Set<String> EXEMPT_PATHS = Set.of("/auth/login", "/users"
      // registration endpoint
  );

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String method = request.getMethod();
    String path = request.getRequestURI();

    // Skip CSRF validation for safe methods or exempt paths
    if (!PROTECTED_METHODS.contains(method) || EXEMPT_PATHS.contains(path)) {
      filterChain.doFilter(request, response);
      return;
    }

    // Validate CSRF token for protected endpoints
    if (!csrfService.validateCsrfToken(request)) {
      log.warn("CSRF token validation failed for {} {}", method, path);
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.getWriter()
              .write("{\"error\":\"CSRF token validation failed\"}");
      response.setContentType("application/json");
      return;
    }

    filterChain.doFilter(request, response);
  }
}
