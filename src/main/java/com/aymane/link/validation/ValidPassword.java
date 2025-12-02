package com.aymane.link.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Documented
@Constraint(validatedBy = {})
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@NotBlank(message = "{error.field.required}")
@Size(min = 8, max = 20, message = "{error.user.password.size}")
@Pattern(regexp = ".*[A-Z].*", message = "{error.user.password.uppercase}")
@Pattern(regexp = ".*[a-z].*", message = "{error.user.password.lowercase}")
@Pattern(regexp = ".*\\d.*", message = "{error.user.password.digit}")
@Pattern(regexp = ".*[~!@#$%^&*()_+={}\\[\\]|\\\\:;\"'<>,.?].*", message = "{error.user.password.special}")
public @interface ValidPassword {

  String message() default "{error.user.password.valid}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
