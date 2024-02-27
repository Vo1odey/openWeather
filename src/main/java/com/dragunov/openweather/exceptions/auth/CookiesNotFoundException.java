package com.dragunov.openweather.exceptions.auth;

import jakarta.servlet.ServletException;

public class CookiesNotFoundException extends ServletException {
    public CookiesNotFoundException(String message) {
        super(message);
    }
}
