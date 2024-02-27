package com.dragunov.openweather.exceptions.auth;

import jakarta.servlet.ServletException;

public class InvalidPasswordException extends ServletException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
