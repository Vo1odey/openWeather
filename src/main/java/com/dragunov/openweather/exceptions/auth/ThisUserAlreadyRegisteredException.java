package com.dragunov.openweather.exceptions.auth;

import jakarta.servlet.ServletException;

public class ThisUserAlreadyRegisteredException extends ServletException{
    public ThisUserAlreadyRegisteredException(String message) {
        super(message);
    }
}
