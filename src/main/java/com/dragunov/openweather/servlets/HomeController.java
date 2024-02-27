package com.dragunov.openweather.servlets;

import com.dragunov.openweather.repository.LocationRepository;
import com.dragunov.openweather.repository.SessionRepository;
import com.dragunov.openweather.models.Location;
import com.dragunov.openweather.models.User;
import com.dragunov.openweather.service.ApiService;
import com.dragunov.openweather.service.modelapi.WeatherDTO;
import com.dragunov.openweather.utils.ThymeleafUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.ITemplateEngine;

import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@WebServlet(name = "AccountPage", value = "/home")
public class HomeController extends HttpServlet {
    private ApiService apiService;
    private SessionRepository sessionRepository;
    private ITemplateEngine templateEngine;
    private LocationRepository locationRepository;
    private WebContext context;
    @Override
    public void init(ServletConfig config) throws ServletException {
        apiService = (ApiService) config.getServletContext().getAttribute("apiService");
        sessionRepository = (SessionRepository) config.getServletContext().getAttribute("sessionRepository");
        locationRepository = (LocationRepository) config.getServletContext().getAttribute("locationRepository");
        templateEngine = (ITemplateEngine) config.getServletContext().getAttribute("templateEngine");
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        context = ThymeleafUtil.buildWebContext(req, resp, getServletContext());
        Cookie[] cookies = req.getCookies();
        String userID = "1";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("UUID")){
                userID = cookie.getValue();
                break;
            }
        }
        try {
            User currentUser = sessionRepository.getSessions(userID).get().getUser();
            context.setVariable("login", currentUser.getLogin());
            List<Location> locations = currentUser.getLocations();
            ArrayList<WeatherDTO> locationsWeathers = new ArrayList<>();
            for (Location location : locations) {
                WeatherDTO weatherDTO = apiService.getLocationByLonLat(location.getLatitude(), location.getLongitude());
                Double temperature = weatherDTO.getTemperature().getTemperature();
                Double celsiusTemperature = apiService.conversionToCelsius(temperature);
                weatherDTO.getTemperature().setTemperature(celsiusTemperature);
                locationsWeathers.add(weatherDTO);
            }
            context.setVariable("locationsWeathers", locationsWeathers);
            templateEngine.process("accountPage", context, resp.getWriter());
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("post");
        context = ThymeleafUtil.buildWebContext(req, resp, getServletContext());
        String ls = (String) req.getParameter("signOut");
        log.error(ls);
        Cookie[] cookies = req.getCookies();
        String userID = "1";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("UUID")){
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
            resp.sendRedirect("/openWeather");
        }
        double longitude = 0;
        double latitude = 0;

        if (req.getParameter("longitude") != null) {
            longitude = Double.parseDouble(req.getParameter("longitude"));
        }
        if (req.getParameter("latitude") != null) {
            latitude = Double.parseDouble(req.getParameter("latitude"));
        }
        if (sessionRepository.getSessions(userID).isPresent()) {
            User user = sessionRepository.getSessions(userID).get().getUser();
            if ((longitude != 0) && (latitude != 0)) {
                log.info("removing location");
                locationRepository.removeLocation(longitude, latitude, user);
                resp.sendRedirect("/home");
            }
        }
    }
}
