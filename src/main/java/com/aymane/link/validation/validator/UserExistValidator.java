package com.aymane.link.validation.validator;

import com.aymane.link.repository.UserRepository;
import com.aymane.link.validation.UserExist;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class UserExistValidator implements ConstraintValidator<UserExist, String> {

  private final UserRepository userRepository;

  @Autowired
  public UserExistValidator(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public boolean isValid(String id, ConstraintValidatorContext context) {
    if (id == null || id.trim()
                        .isEmpty()) {
      return true;
    }
    try {
      Long numericId = Long.parseLong(id);
      return userRepository.findById(numericId)
                           .isPresent();
    } catch (NumberFormatException e) {
      log.info("Failed converting {} to Long", id);
      return false;
    }
  }
}
