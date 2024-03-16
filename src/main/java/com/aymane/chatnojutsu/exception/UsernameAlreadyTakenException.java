package com.aymane.chatnojutsu.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UsernameAlreadyTakenException extends RuntimeException {
    public UsernameAlreadyTakenException(String message) {
        super(message);
    }
}
