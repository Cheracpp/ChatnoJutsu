package com.aymane.link.exception.security;

public class CsrfTokenException extends RuntimeException {

  public CsrfTokenException(String message) {
    super(message);
  }
}
