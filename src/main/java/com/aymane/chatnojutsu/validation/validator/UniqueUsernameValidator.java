package com.aymane.chatnojutsu.validation.validator;

import com.aymane.chatnojutsu.repository.UserRepository;
import com.aymane.chatnojutsu.validation.UniqueUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

  @Autowired
  private UserRepository userRepository;

  @Override
  public boolean isValid(String username, ConstraintValidatorContext context) {
    if (username == null || username.trim().isEmpty()) {
      return true;
    }

    return !userRepository.existsByUsername(username);
  }
}
