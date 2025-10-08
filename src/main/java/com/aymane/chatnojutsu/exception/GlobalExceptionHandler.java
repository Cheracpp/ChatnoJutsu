package com.aymane.chatnojutsu.exception;

import jakarta.validation.ConstraintViolation;
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
    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, List<Map<String, String>>>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {

    List<Map<String, String>> errorDetails = new ArrayList<>();

    ex.getBindingResult().getAllErrors().forEach((error) -> {
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

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        log.error(HttpStatus.CONFLICT + " " + e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage()));
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e){
        log.error(HttpStatus.BAD_REQUEST + " " + e.getMessage());
        List<String> errorMessages = new ArrayList<>();
        for(ConstraintViolation<?> violation : e.getConstraintViolations()){
            errorMessages.add(violation.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),errorMessages.toString()));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e){
        log.error(HttpStatus.BAD_REQUEST + " " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }
    @ExceptionHandler(PasswordFormatException.class)
    public ResponseEntity<ErrorResponse> handlePasswordFormatException(PasswordFormatException e){
        log.error(HttpStatus.BAD_REQUEST + " " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),e.getMessage()));
    }
    Map<String, List<Map<String, String>>> response = new HashMap<>();
    response.put("errors", errorDetails);

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, FieldErrorDetail>> handleConstraintViolationException(
      ConstraintViolationException ex) {

    Map<String, FieldErrorDetail> errors = new HashMap<>();

    ex.getConstraintViolations().forEach((violation) -> {
      String fieldName = violation.getPropertyPath().toString();
      if (fieldName.contains(".")) {
        fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
      }
      String errorMessage = violation.getMessage();
      String errorCode = violation.getConstraintDescriptor().getAnnotation().annotationType()
          .getSimpleName();

      errors.put(fieldName, new FieldErrorDetail(errorMessage, errorCode));
    });

    return ResponseEntity.badRequest().body(errors);
  }

}
