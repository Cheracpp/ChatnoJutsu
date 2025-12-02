package com.aymane.link.validation.validator;

import com.aymane.link.repository.UserRepository;
import com.aymane.link.validation.UniqueUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

  private final UserRepository userRepository;

  @Autowired
  public UniqueUsernameValidator(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public boolean isValid(String username, ConstraintValidatorContext context) {
    if (username == null || username.trim()
                                    .isEmpty()) {
      return true;
    }

    return !userRepository.existsByUsername(username);
  }
}
