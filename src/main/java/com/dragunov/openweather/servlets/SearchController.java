package com.dragunov.openweather.servlets;

import com.dragunov.openweather.repository.LocationRepository;
import com.dragunov.openweather.repository.SessionRepository;
import com.dragunov.openweather.models.Location;
import com.dragunov.openweather.models.User;
import com.dragunov.openweather.service.ApiService;
import com.dragunov.openweather.service.modelapi.LocationDTO;
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

@Slf4j
@WebServlet(name = "searchPage", value = "/search/*")
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            log.info("get search");
            context = ThymeleafUtil.buildWebContext(req, resp, getServletContext());
            String locationName = req.getParameter("q");
            ArrayList<LocationDTO> locationsNames = (ArrayList<LocationDTO>) apiService.getAvailableLocationNames(locationName);
            context.setVariable("locations", locationsNames);

            templateEngine.process("search", context, resp.getWriter());


        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("post search");
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
            locationRepository.saveLocation(currentUser, locationForCurrentUser);



            resp.sendRedirect("/home");
//            templateEngine.process("accountPage", context, resp.getWriter());
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
