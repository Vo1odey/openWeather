package com.dragunov.openweather.servlets.auth;

import com.dragunov.openweather.repository.UserRepository;
import com.dragunov.openweather.exceptions.auth.LoginTooShortException;
import com.dragunov.openweather.exceptions.auth.PasswordTooShortException;
import com.dragunov.openweather.exceptions.auth.PasswordsNotEqualsException;
import com.dragunov.openweather.exceptions.auth.ThisUserAlreadyRegisteredException;
import com.dragunov.openweather.models.User;
import com.dragunov.openweather.service.UserService;
import com.dragunov.openweather.utils.ThymeleafUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
@Slf4j
@WebServlet(name="Register", value = "/register")
public class RegisterController extends HttpServlet {
    private ITemplateEngine templateEngine;
    private WebContext context;
    private UserService userService;
    private UserRepository userRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        userService = (UserService) config.getServletContext().getAttribute("userService");
        templateEngine = (ITemplateEngine) config.getServletContext().getAttribute("templateEngine");
        userRepository = (UserRepository) config.getServletContext().getAttribute("userRepository");
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        context = ThymeleafUtil.buildWebContext(req, resp, getServletContext());
        templateEngine.process("register", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        context = ThymeleafUtil.buildWebContext(req, resp, getServletContext());
        String login = req.getParameter("login");
        String firstPassword = req.getParameter("firstPassword");
        String secondPassword = req.getParameter("secondPassword");
        try {
            userService.signUp(login, firstPassword, secondPassword);
            if (userRepository.getUser(login).isPresent()) {
                User user = userRepository.getUser(login).get();
                Cookie userCookie = new Cookie("UUID", user.getSessions().get(0).getId());
                userCookie.setMaxAge(-1);
                resp.addCookie(userCookie);
                resp.sendRedirect(req.getContextPath() + "/home");
            } else {
                log.error("User not found in database");
            }
        } catch (ThisUserAlreadyRegisteredException e) {
            log.error("This user already registered");
            context.setVariable("loginAlready", "This login already registered");
            templateEngine.process("register", context, resp.getWriter());
        } catch (PasswordsNotEqualsException e) {
            log.error("passwords is not equals");
            context.setVariable("passwordNotEq", "Passwords is not equals");
            templateEngine.process("register", context, resp.getWriter());
        } catch (LoginTooShortException e) {
            log.error("Login too short");
            context.setVariable("loginTooShort", "Login too short");
            templateEngine.process("register", context, resp.getWriter());
        } catch (PasswordTooShortException e) {
            log.error("Password too short");
            context.setVariable("passwordTooShort", "Password too short");
            templateEngine.process("register", context, resp.getWriter());
        }
    }
}
