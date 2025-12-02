package com.aymane.link.dto;

import com.aymane.link.validation.ValidEmail;
import com.aymane.link.validation.ValidPassword;
import com.aymane.link.validation.ValidUsername;

public record RegisterRequest(@ValidUsername String username, @ValidPassword String password,
                              @ValidEmail String email) {

}
