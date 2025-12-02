package com.aymane.link.dto;

import com.aymane.link.validation.ValidPassword;

public record LoginRequest(String username, @ValidPassword String password) {

}
