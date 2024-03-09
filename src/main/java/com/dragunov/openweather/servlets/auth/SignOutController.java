package com.dragunov.openweather.servlets.auth;

import com.dragunov.openweather.DAO.SessionRepository;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
@Slf4j
@WebServlet(name = "SignOut", urlPatterns = "/sign-out")
public class SignOutController extends HttpServlet {
    private SessionRepository sessionRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        sessionRepository = (SessionRepository) config.getServletContext().getAttribute("sessionRepository");
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        String userID = "1";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("UUID")) {
                userID = cookie.getValue();
                break;
            }
        }
        Cookie userCookie;
        if (req.getParameter("signOut") != null) {
            log.info("remove session");
            sessionRepository.removeSessionById(userID);
            userCookie = new Cookie("UUID", "");
            userCookie.setMaxAge(0);
            resp.addCookie(userCookie);
            log.info("remove cookie");
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
