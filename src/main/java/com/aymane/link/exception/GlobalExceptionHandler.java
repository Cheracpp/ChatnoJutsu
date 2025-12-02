package com.aymane.link.exception;

import com.aymane.link.dto.ErrorResponse;
import com.aymane.link.exception.security.CsrfTokenException;
import com.aymane.link.exception.user.FriendServiceException;
import com.aymane.link.exception.user.UserNotFoundException;
import com.aymane.link.util.FieldErrorDetail;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  MessageSource messageSource;

  @Autowired
  public GlobalExceptionHandler(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex) {
    Map<String, String> error = new HashMap<>();
    Locale locale = LocaleContextHolder.getLocale();
    String errorMessage = messageSource.getMessage("error.request.body.missing", null, locale);
    error.put("error", errorMessage);
    return ResponseEntity.badRequest()
                         .body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, List<Map<String, String>>>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {

    List<Map<String, String>> errorDetails = new ArrayList<>();

    ex.getBindingResult()
      .getAllErrors()
      .forEach((error) -> {
        if (error instanceof FieldError) {
          Map<String, String> errorDetail = new HashMap<>();
          String fieldName = ((FieldError) error).getField();
          String errorMessage = error.getDefaultMessage();

          String errorCode = error.getCode();

          errorDetail.put("field", fieldName);
          errorDetail.put("errorCode", errorCode);
          errorDetail.put("errorMessage", errorMessage);
          errorDetails.add(errorDetail);
        }
      });

    Map<String, List<Map<String, String>>> response = new HashMap<>();
    response.put("errors", errorDetails);

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, FieldErrorDetail>> handleConstraintViolationException(
      ConstraintViolationException ex) {

    Map<String, FieldErrorDetail> errors = new HashMap<>();

    ex.getConstraintViolations()
      .forEach((violation) -> {
        String fieldName = violation.getPropertyPath()
                                    .toString();
        if (fieldName.contains(".")) {
          fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
        }
        String errorMessage = violation.getMessage();
        String errorCode = violation.getConstraintDescriptor()
                                    .getAnnotation()
                                    .annotationType()
                                    .getSimpleName();

        errors.put(fieldName, new FieldErrorDetail(errorMessage, errorCode));
      });

    return ResponseEntity.badRequest()
                         .body(errors);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
    ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "User Not Found",
        ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(FriendServiceException.class)
  public ResponseEntity<ErrorResponse> handleFriendServiceException(FriendServiceException ex) {
    ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid Request",
        ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(CsrfTokenException.class)
  public ResponseEntity<ErrorResponse> handleCsrfTokenException(CsrfTokenException ex) {
    ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Server Error", ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
