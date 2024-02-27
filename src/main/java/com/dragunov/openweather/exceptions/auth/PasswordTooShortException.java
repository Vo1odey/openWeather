package com.dragunov.openweather.exceptions.auth;

import jakarta.servlet.ServletException;

public class PasswordTooShortException extends ServletException {
    public PasswordTooShortException(String message) {
        super(message);
    }
}
