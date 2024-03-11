package com.dragunov.openweather.service;

import com.dragunov.openweather.DAO.SessionRepository;
import com.dragunov.openweather.DAO.UserRepository;
import com.dragunov.openweather.exceptions.auth.*;
import com.dragunov.openweather.models.Sessions;
import com.dragunov.openweather.models.User;
import com.dragunov.openweather.utils.BCryptUtil;
import com.dragunov.openweather.utils.Validator;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;


    public UserService(UserRepository userRepository, SessionRepository sessionRepository){
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public User signIn(String login, String password) throws UserNotFoundException, InvalidPasswordException
            ,LoginTooShortException, PasswordTooShortException {
        Validator.checkLengthLogin(login);
        Validator.checkLengthPassword(password);
        if (userRepository.getUser(login).isEmpty()) {
            log.error("This user not at database " + login);
            throw new UserNotFoundException("this user not registered " + login);
        }
        User user = userRepository.getUser(login).get();
        String hashedPassword = user.getPassword();
        if (BCryptUtil.isHashedPasswordCorrect(password, hashedPassword)) {
            log.info("hashed password correct");
            createNewSession(login);
            return user;
        } else {
            log.error("invalid password for user " + login);
            throw new InvalidPasswordException("invalid password");
        }
    }
    public void signUp(String login, String firstPassword, String secondPassword) throws ThisUserAlreadyRegisteredException
            ,LoginTooShortException, PasswordTooShortException, PasswordsNotEqualsException {
        if (userRepository.getUser(login).isPresent()) {
            throw new ThisUserAlreadyRegisteredException("this user already registered");
        }
        Validator.checkLengthLogin(login);
        Validator.checkLengthPassword(firstPassword);
        if (firstPassword.equals(secondPassword)) {
            String hashedPassword = BCryptUtil.hashPassword(firstPassword);
            User user = new User(login, hashedPassword);
            userRepository.saveUser(user);
            createNewSession(login);
        } else {
            throw new PasswordsNotEqualsException("passwords not equals");
        }
    }
    private void createNewSession(String login){
        int sessionTime = 15;
        String uuid = String.valueOf(UUID.randomUUID());
        Sessions sessionsForThisUser = new Sessions(uuid, LocalDateTime.now().plusMinutes(sessionTime));
        sessionRepository.saveCurrentSession(login, sessionsForThisUser);
        log.info("create a new session - {}, session time = {}", sessionsForThisUser, sessionTime);
    }
}
