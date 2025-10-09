package com.aymane.chatnojutsu.validation.validator;

import com.aymane.chatnojutsu.repository.UserRepository;
import com.aymane.chatnojutsu.validation.UniqueEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

  private final UserRepository userRepository;

  @Autowired
  public UniqueEmailValidator(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.trim().isEmpty()) {
      return true;
    }

    return !userRepository.existsByEmail(value);
  }
}
