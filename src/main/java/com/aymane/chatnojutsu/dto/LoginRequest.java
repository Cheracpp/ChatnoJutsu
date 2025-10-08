package com.aymane.chatnojutsu.dto;

import com.aymane.chatnojutsu.validation.ValidPassword;

public record LoginRequest(String username, @ValidPassword String password) {

}
