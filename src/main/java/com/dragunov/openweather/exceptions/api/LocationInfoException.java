package com.dragunov.openweather.exceptions.api;

import jakarta.servlet.ServletException;

public class LocationInfoException extends ServletException {
    public LocationInfoException (String message){
        super(message);
    }
}
