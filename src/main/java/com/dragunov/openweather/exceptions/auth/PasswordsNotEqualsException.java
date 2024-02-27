package com.dragunov.openweather.exceptions.auth;

import jakarta.servlet.ServletException;

public class PasswordsNotEqualsException extends ServletException {
    public PasswordsNotEqualsException(String message){
        super(message);
    }
}
