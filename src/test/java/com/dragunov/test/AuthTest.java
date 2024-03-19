package com.dragunov.test;

import com.dragunov.openweather.repository.SessionRepository;
import com.dragunov.openweather.repository.UserRepository;
import com.dragunov.openweather.exceptions.auth.*;
import com.dragunov.openweather.models.Sessions;
import com.dragunov.openweather.models.User;
import com.dragunov.openweather.service.UserService;
import com.dragunov.openweather.utils.BCryptUtil;
import com.dragunov.openweather.utils.Validator;
import com.dragunov.utils.HibernateUtilTest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class AuthTest {
    static UserRepository userRepository = new UserRepository(HibernateUtilTest.getSessionFactory());
    static SessionRepository sessionRepository = new SessionRepository(HibernateUtilTest.getSessionFactory());
    static UserService userService = new UserService(userRepository, sessionRepository);
    static User user = new User("mail.test@gmail.com", BCryptUtil.hashPassword("somePassword"));
    static HttpServletRequest request;

    @BeforeAll
    static void initializedTestObject() throws PasswordsNotEqualsException, LoginTooShortException
            , ThisUserAlreadyRegisteredException, PasswordTooShortException {
        userService.signUp(user.getLogin(), user.getPassword(), user.getPassword());
        request = mock(HttpServletRequest.class);
    }
    /*
     *** SignUp tests ***
     */
    @Test
    void must_ThrowThisUserAlreadyRegisteredException_WhenRegisterExistingEmail() {
        Assertions.assertThrows(ThisUserAlreadyRegisteredException.class,
                () -> userService.signUp("mail.test@gmail.com", "somePassword", "somePassword"));
    }
    @Test
    void must_ThrowLoginTooShortException_WhenShortLogin() {
        User anotherUser = new User("mai", "somePassword");
        Assertions.assertThrows(LoginTooShortException.class,
                () -> userService.signUp(anotherUser.getLogin(), anotherUser.getPassword(), anotherUser.getPassword()));
        userRepository.removeUser(anotherUser);
    }
    @Test
    void must_ThrowPasswordTooShortException_WhenShortPassword() {
        User anotherUser = new User("mail.another0Test@gmail.com", "some");
        Assertions.assertThrows(PasswordTooShortException.class,
                () -> userService.signUp(anotherUser.getLogin(), anotherUser.getPassword(), anotherUser.getPassword()));
        userRepository.removeUser(anotherUser);
    }
    @Test
    void must_ThrowPasswordNotEqualsException_WhenPasswordNotEquals() {
        User anotherUser = new User("mail.another1Test@gmail.com", "somePassword");
        Assertions.assertThrows(PasswordsNotEqualsException.class,
                () -> userService.signUp(anotherUser.getLogin(), anotherUser.getPassword(), "anotherPassword"));
        userRepository.removeUser(anotherUser);
    }
    @Test
    void must_SaveNewUserInDataBase_WhenCorrectEmail() throws ThisUserAlreadyRegisteredException
            ,PasswordsNotEqualsException, LoginTooShortException, PasswordTooShortException {
        User anotherUser = new User("mail.another2Test@gmail.com", "somePassword");
        userService.signUp(anotherUser.getLogin(), anotherUser.getPassword(), anotherUser.getPassword());
        Assertions.assertTrue(userRepository.getUser(anotherUser.getLogin()).isPresent());
    }
    /*
     *** SignIn tests ***
     */
    @Test
    void must_ThrowUserNotFoundException_WhenUserNotFoundAtDatabase() {
        User anotherUser = new User("mail.another3Test@gmail.com", "somePassword");
        Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.signIn(anotherUser.getLogin(), anotherUser.getPassword()));
        userRepository.removeUser(anotherUser);
    }
    @Test
    void must_ThrowInvalidPasswordException_WhenInvalidPasswordForUser() {
        Assertions.assertThrows(InvalidPasswordException.class,
                () -> userService.signIn(user.getLogin(), "123456"));
    }
    @Test
    void must_ThrowLoginTooShortException_WhenEmptyLogin() {
        User anotherUser = new User("", "somePassword");
        Assertions.assertThrows(LoginTooShortException.class,
                () -> userService.signIn(anotherUser.getLogin() , anotherUser.getPassword()));
        userRepository.removeUser(anotherUser);
    }
    @Test
    void must_ThrowPasswordTooShortException_WhenEmptyPassword() {
        User anotherUser = new User("mail.another4Test@gmail.com", "");
        Assertions.assertThrows(PasswordTooShortException.class,
                () -> userService.signIn(anotherUser.getLogin(), anotherUser.getPassword()));
        userRepository.removeUser(anotherUser);
    }
    @Test
    void must_ReturnUserId_WhenCorrectSignIn() throws UserNotFoundException, LoginTooShortException
            ,InvalidPasswordException, PasswordTooShortException {
        Assertions.assertEquals(userRepository.getUser(user.getLogin()).get().getId(), userService.signIn(
                user.getLogin(), user.getPassword()).getId());
    }
    /*
     *** Session tests ***
     */
    @Test
    void must_IsActualSession_WhenCookieContainsSessionId() {
        User testUser = userRepository.getUser(user.getLogin()).get();
        Sessions sessions = testUser.getSessions().get(0);
        Cookie[] userCookie = {new Cookie("UUID", sessions.getId())};
        when(request.getCookies()).thenReturn(userCookie);
    }
    @Test
    void must_DeletingSession_WhenSessionTimeOut() {
        User testUser = userRepository.getUser(user.getLogin()).get();
        Sessions sessions = testUser.getSessions().get(0);
        Assertions.assertThrows(SessionTimeOutException.class,() -> Validator.isSessionTimeInvalid(sessions.getExpiresAt()
                , LocalDateTime.now().plusHours(25)));
        sessionRepository.removeSessionById(sessions.getId());
    }
}
