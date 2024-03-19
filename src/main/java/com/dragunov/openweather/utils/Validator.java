package com.dragunov.openweather.utils;

import com.dragunov.openweather.exceptions.auth.LoginTooShortException;
import com.dragunov.openweather.exceptions.auth.PasswordTooShortException;
import com.dragunov.openweather.exceptions.auth.SessionTimeOutException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
@Slf4j
public class Validator {
    public static String checkLengthLogin(String login) throws LoginTooShortException {
        if (login.length() < 4) {
            log.error("Too short login");
            throw new LoginTooShortException("length < 4");
        }
        log.info("login is correct");
        return login;
    }

    public static String checkLengthPassword(String password) throws PasswordTooShortException {
        if (password.length() < 6) {
            log.error("Too short password");
            throw new PasswordTooShortException("length < 6");
        }
        log.info("password is correct");
        return password;
    }

    public static boolean isSessionTimeInvalid(LocalDateTime localDateTimeFromDB, LocalDateTime currentTime) throws SessionTimeOutException {
        log.error("current time {}, time from database {}", currentTime, localDateTimeFromDB);
        log.error("result = {}", localDateTimeFromDB.isBefore(currentTime));
        if (localDateTimeFromDB.isBefore(currentTime)) {
            throw new SessionTimeOutException("Session time out");
        } else {
            return localDateTimeFromDB.isAfter(currentTime);
        }
    }

    public static String spaceReplacePlusForApi(String name){
        String trimmed = name.trim();
        char[] charsFromTrimmed = trimmed.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (char chars : charsFromTrimmed) {
            if (chars == ' '){
                chars = '+';
            }
            builder.append(chars);
        }
        return builder.toString();
    }
}

