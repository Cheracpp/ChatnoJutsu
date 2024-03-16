package com.aymane.chatnojutsu.exception;


public record ErrorResponse(int statusCode, String message) {
}