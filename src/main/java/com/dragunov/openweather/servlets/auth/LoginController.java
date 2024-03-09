package com.dragunov.openweather.servlets.auth;

import com.dragunov.openweather.exceptions.auth.InvalidPasswordException;
import com.dragunov.openweather.exceptions.auth.LoginTooShortException;
import com.dragunov.openweather.exceptions.auth.PasswordTooShortException;
import com.dragunov.openweather.exceptions.auth.UserNotFoundException;
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
@WebServlet(name="Login", value = "/login")
public class LoginController extends HttpServlet {
    private ITemplateEngine templateEngine;
    private WebContext context;
    private UserService userService;
    @Override
    public void init(ServletConfig config) throws ServletException {
        userService = (UserService) config.getServletContext().getAttribute("userService");
        templateEngine = (ITemplateEngine) config.getServletContext().getAttribute("templateEngine");
        super.init(config);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        context = ThymeleafUtil.buildWebContext(req, resp, getServletContext());
        context.setVariable("contextPath", req.getContextPath());
        templateEngine.process("Login", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        context = ThymeleafUtil.buildWebContext(req, resp, getServletContext());
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        try {
            User user = userService.signIn(login, password);
            Cookie userCookie = new Cookie("UUID", user.getSessions().get(0).getId());
            userCookie.setMaxAge(-1);
            resp.addCookie(userCookie);
            resp.sendRedirect(req.getContextPath() + "/home");
        } catch (UserNotFoundException | LoginTooShortException e) {
            log.error("This user not found");
            context.setVariable("errorLogin", "invalid login");
            templateEngine.process("Login", context, resp.getWriter());
        } catch (InvalidPasswordException | PasswordTooShortException e) {
            log.error("Invalid password ");
            context.setVariable("errorPassword", "invalid password");
            templateEngine.process("Login", context, resp.getWriter());
        }
    }
}