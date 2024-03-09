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
import com.dragunov.openweather.service.modelapi.LocationDTO;
import com.dragunov.openweather.service.modelapi.WeatherDTO;
import com.dragunov.openweather.utils.ThymeleafUtil;
import com.dragunov.openweather.utils.Validator;
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

@Slf4j
@WebServlet(name = "searchPage", value = "/search/*")
//adding /search at for each search location
public class SearchController extends HttpServlet {
    private ITemplateEngine templateEngine;
    private WebContext context;
    private ApiService apiService;
    private SessionRepository sessionRepository;
    private LocationRepository locationRepository;
    @Override
    public void init(ServletConfig config) throws ServletException {
        apiService = (ApiService) config.getServletContext().getAttribute("apiService");
        templateEngine = (ITemplateEngine) config.getServletContext().getAttribute("templateEngine");
        sessionRepository = (SessionRepository) config.getServletContext().getAttribute("sessionRepository");
        locationRepository = (LocationRepository) config.getServletContext().getAttribute("locationRepository");
        super.init(config);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            context = ThymeleafUtil.buildWebContext(req, resp, getServletContext());
            context.setVariable("contextPath", req.getContextPath());
            String validCityName = Validator.spaceReplacePlusForApi(req.getParameter("q"));
            ArrayList<LocationDTO> locationsNames = (ArrayList<LocationDTO>) apiService.getAvailableLocationNames(validCityName);
            context.setVariable("locations", locationsNames);
            templateEngine.process("Search", context, resp.getWriter());
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BadRequestException | LocationInfoException | ApiKeyException | CallsPerMinuteException e) {
            context.setVariable("locationPercentError", "bad request");
            templateEngine.process("Search", context, resp.getWriter());
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        context = ThymeleafUtil.buildWebContext(req, resp, getServletContext());
        Double longitude = Double.parseDouble(req.getParameter("longitude"));
        Double latitude = Double.parseDouble(req.getParameter("latitude"));
        Cookie[] cookies = req.getCookies();
        String userID = "1";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("UUID")){
                userID = cookie.getValue();
                break;
            }
        }
        User currentUser = sessionRepository.getSessions(userID).get().getUser();
        try {
            WeatherDTO desiredLocation = apiService.getLocationByLonLat(latitude, longitude);
            Location locationForCurrentUser = new Location(desiredLocation.getName()
                    ,desiredLocation.getCoordinates().getLatitude()
                    ,desiredLocation.getCoordinates().getLongitude());
            if (locationRepository.getLocation(currentUser, locationForCurrentUser).isEmpty()){
                locationRepository.saveLocation(currentUser, locationForCurrentUser);
                resp.sendRedirect(req.getContextPath() + "/home");
            } else if (locationRepository.getLocation(currentUser, locationForCurrentUser).isPresent()){
                context.setVariable("locationPercentError", "this location already using for this user");
                templateEngine.process("Search", context, resp.getWriter());
            }
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BadRequestException | LocationInfoException | ApiKeyException | CallsPerMinuteException e) {
            context.setVariable("locationPercentError", "bad request");
            templateEngine.process("Search", context, resp.getWriter());
        }
    }
}
