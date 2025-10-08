package com.aymane.chatnojutsu.dto;

import com.aymane.chatnojutsu.validation.ValidEmail;
import com.aymane.chatnojutsu.validation.ValidPassword;
import com.aymane.chatnojutsu.validation.ValidUsername;

public record RegisterRequest(@ValidUsername String username, @ValidPassword String password,
                              @ValidEmail String email) {

}
