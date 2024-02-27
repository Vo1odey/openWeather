package com.dragunov.openweather.exceptions.auth;

import jakarta.servlet.ServletException;

public class UserIdFromCookiesNotFoundException extends ServletException {
    public UserIdFromCookiesNotFoundException(String message){
        super(message);
    }
}
