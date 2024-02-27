package com.dragunov.openweather.utils;


import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
@Slf4j
public class BCryptUtil {
    public static String hashPassword(String password) {
        log.info("password is hashed");
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    public static boolean isHashedPasswordCorrect(String password, String hashedPassword) {
        log.info("check password and hashed password -> " + BCrypt.checkpw(password, hashedPassword));
        return BCrypt.checkpw(password, hashedPassword);
    }
}
