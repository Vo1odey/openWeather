package com.dragunov.openweather.exceptions.api;

import java.rmi.ServerException;

public class BadRequestException extends ServerException {
    public BadRequestException(String message) {
        super(message);
    }
}
