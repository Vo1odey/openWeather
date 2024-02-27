package com.dragunov.openweather.exceptions.auth;

import jakarta.servlet.ServletException;

public class SessionTimeOutException extends ServletException {
    public SessionTimeOutException(String message) {
        super(message);
    }
}
