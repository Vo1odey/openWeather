package com.dragunov.openweather.exceptions.api;

import java.rmi.ServerException;

public class CallsPerMinuteException extends ServerException {
    public CallsPerMinuteException(String message) {
        super(message);
    }
}
