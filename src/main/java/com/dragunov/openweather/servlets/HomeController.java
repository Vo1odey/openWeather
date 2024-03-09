package com.dragunov.openweather.servlets;

import com.dragunov.openweather.DAO.LocationRepository;
import com.dragunov.openweather.DAO.SessionRepository;
import com.dragunov.openweather.exceptions.api.ApiKeyException;
import com.dragunov.openweather.exceptions.api.BadRequestException;
import com.dragunov.openweather.exceptions.api.CallsPerMinuteException;
import com.dragunov.openweather.exceptions.api.LocationInfoException;
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
@WebServlet(name = "Home", value = "/home")
public class HomeController extends HttpServlet {
    private ApiService apiService;
    private SessionRepository sessionRepository;
    private ITemplateEngine templateEngine;
    private LocationRepository locationRepository;

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
        WebContext context = ThymeleafUtil.buildWebContext(req, resp, getServletContext());
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
                Double temperatureMax = weatherDTO.getTemperature().getTempMax();
                Double temperatureMin = weatherDTO.getTemperature().getTempMin();
                Double celsiusTemperatureMax = apiService.conversionToCelsius(temperatureMax);
                Double celsiusTemperatureMin = apiService.conversionToCelsius(temperatureMin);
                weatherDTO.getTemperature().setTempMax(celsiusTemperatureMax);
                weatherDTO.getTemperature().setTempMin(celsiusTemperatureMin);
                locationsWeathers.add(weatherDTO);
            }
            context.setVariable("contextPath", req.getContextPath());
            context.setVariable("locationsWeathers", locationsWeathers);
            templateEngine.process("Home", context, resp.getWriter());
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BadRequestException | LocationInfoException | ApiKeyException | CallsPerMinuteException e) {
            context.setVariable("locationPercentError", "bad request");
            templateEngine.process("Search", context, resp.getWriter());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        String userID = "1";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("UUID")){
                userID = cookie.getValue();
                break;
            }
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
                resp.sendRedirect(req.getContextPath() + "/home");
            }
        }
    }
}
