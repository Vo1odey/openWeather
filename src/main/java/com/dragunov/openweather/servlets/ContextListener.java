package com.dragunov.openweather.servlets;

import com.dragunov.openweather.repository.LocationRepository;
import com.dragunov.openweather.repository.SessionRepository;
import com.dragunov.openweather.repository.UserRepository;
import com.dragunov.openweather.service.ApiService;
import com.dragunov.openweather.service.UserService;
import com.dragunov.openweather.utils.HibernateUtil;
import com.dragunov.openweather.utils.ThymeleafUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.flywaydb.core.Flyway;
import org.thymeleaf.TemplateEngine;

@WebListener
public class ContextListener implements ServletContextListener {
    public ContextListener() {
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Flyway flyway = Flyway.configure().baselineOnMigrate(true).dataSource("jdbc:postgresql://db:5432/open_weather"
                ,"postgres", "postgres").load();
        flyway.migrate();
        ServletContext context = sce.getServletContext();
        UserRepository userRepository = new UserRepository(HibernateUtil.getSessionFactory());
        context.setAttribute("userRepository", userRepository);
        LocationRepository locationRepository = new LocationRepository(HibernateUtil.getSessionFactory());
        context.setAttribute("locationRepository", locationRepository);
        SessionRepository sessionRepository = new SessionRepository(HibernateUtil.getSessionFactory());
        context.setAttribute("sessionRepository", sessionRepository);
        UserService userService = new UserService(userRepository, sessionRepository);
        context.setAttribute("userService", userService);
        ApiService apiService = new ApiService();
        context.setAttribute("apiService", apiService);

        TemplateEngine templateEngine = ThymeleafUtil.buildTemplateEngine(context);
        context.setAttribute("templateEngine", templateEngine);

        context.setRequestCharacterEncoding("UTF-8");
        context.setResponseCharacterEncoding("UTF-8");
    }
}