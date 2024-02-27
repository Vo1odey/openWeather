package com.dragunov.openweather.exceptions.auth;

import jakarta.servlet.ServletException;

public class LoginTooShortException extends ServletException {
    public LoginTooShortException(String message) {
        super(message);
    }
}
