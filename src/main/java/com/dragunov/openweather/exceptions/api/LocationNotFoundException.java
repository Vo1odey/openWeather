package com.dragunov.openweather.exceptions.api;

import jakarta.servlet.ServletException;

public class LocationNotFoundException extends ServletException {
    public LocationNotFoundException (String message) {
        super(message);
    }
}
