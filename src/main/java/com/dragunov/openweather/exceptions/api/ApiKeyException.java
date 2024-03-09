package com.dragunov.openweather.exceptions.api;

import jakarta.servlet.ServletException;

public class ApiKeyException extends ServletException {
    public ApiKeyException(String message){
        super(message);
    }
}
