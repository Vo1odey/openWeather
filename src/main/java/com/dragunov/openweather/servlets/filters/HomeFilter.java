package com.dragunov.openweather.servlets.filters;

import com.dragunov.openweather.DAO.SessionRepository;
import com.dragunov.openweather.exceptions.auth.CookiesNotFoundException;
import com.dragunov.openweather.exceptions.auth.SessionTimeOutException;
import com.dragunov.openweather.exceptions.auth.UserIdFromCookiesNotFoundException;
import com.dragunov.openweather.utils.Validator;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@WebFilter(filterName = "HomeFilter", urlPatterns = {"/home", "/search/*"})
public class HomeFilter implements Filter {
    private SessionRepository sessionRepository;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        sessionRepository = (SessionRepository) filterConfig.getServletContext().getAttribute("sessionRepository");
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String userIdFromCookies = "";
        try {
            log.info("checking cookies");
            if (isPresentCookies(httpRequest)) {
                userIdFromCookies = String.valueOf(findUserIdFromCookies(httpRequest.getCookies()).getValue());
                log.info("get cookie -> {}", userIdFromCookies);
                if (sessionRepository.getSessions(userIdFromCookies).isPresent()) {
                    LocalDateTime timeFromSession = sessionRepository.getSessions(userIdFromCookies).get().getExpiresAt();
                    LocalDateTime currentTime = LocalDateTime.now();
                    if (Validator.isSessionTimeInvalid(timeFromSession, currentTime)) {
                        log.info("done, -> home page");
                        chain.doFilter(request, response);
                    }
                } else {
                    log.warn("session not found");
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
                }
            }
        } catch (UserIdFromCookiesNotFoundException | CookiesNotFoundException e) {
            log.warn("Cookies not found -> login page");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        } catch (SessionTimeOutException e) {
            log.warn("Session time out exception");
            log.info("user id from cookies = {}", userIdFromCookies);
            sessionRepository.removeSessionById(userIdFromCookies);
            httpResponse.addCookie(removeCookie());
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }
    }
    private Cookie findUserIdFromCookies(Cookie[] cookies) throws UserIdFromCookiesNotFoundException {
        Cookie sessionIdCookie = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("UUID")) {
                sessionIdCookie = cookie;
                break;
            }
        }
        if (sessionIdCookie == null) throw new UserIdFromCookiesNotFoundException("Session id from cookies is null");
        return sessionIdCookie;
    }
    private boolean isPresentCookies(HttpServletRequest request) throws CookiesNotFoundException{
        if (request.getCookies() == null) throw new CookiesNotFoundException("Cookies is null");
        else return true;
    }
    private Cookie removeCookie() {
        Cookie removeCookie = new Cookie("UUID", "");
        removeCookie.setMaxAge(0);
        return removeCookie;
    }
    @Override
    public void destroy() {
    }
}

