package com.dragunov.openweather.service;


import com.dragunov.openweather.exceptions.api.ApiKeyException;
import com.dragunov.openweather.exceptions.api.BadRequestException;
import com.dragunov.openweather.exceptions.api.CallsPerMinuteException;
import com.dragunov.openweather.exceptions.api.LocationInfoException;
import com.dragunov.openweather.service.modelapi.WeatherDTO;
import com.dragunov.openweather.service.modelapi.LocationDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.http.HttpClient;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ApiService {
    private final String KEY = "18c261cc8f1e9cbcdf34e23e088dd57f";
    private final String URL_FIND_LOCATION_BY_NAME = "https://api.openweathermap.org/data/2.5/weather";
    private final String URL_FIND_LOCATIONS_BY_NAMES = "https://api.openweathermap.org/geo/1.0/direct";
    private final String URL_FIND_LOCATION_BY_LOT_LAN = "https://api.openweathermap.org/data/2.5/weather";
    public WeatherDTO getWeatherByName(String name) throws URISyntaxException, IOException
            , InterruptedException, ApiKeyException, LocationInfoException, BadRequestException, CallsPerMinuteException{
        HttpRequest request = HttpRequest.newBuilder(new URI(URL_FIND_LOCATION_BY_NAME
                + "?q=" + name
                + "&appid=" + KEY)).GET().build();
        HttpClient client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        checkResponseCode(response);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.body(), WeatherDTO.class);
    }
    public List<LocationDTO> getAvailableLocationNames(String name) throws URISyntaxException, IOException
            , InterruptedException, ApiKeyException, LocationInfoException, BadRequestException, CallsPerMinuteException{
        HttpRequest request = HttpRequest.newBuilder(new URI(URL_FIND_LOCATIONS_BY_NAMES
                + "?q=" + name
                + "&limit=" + "5"
                + "&appid=" + KEY)).GET().build();
        HttpClient client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        checkResponseCode(response);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.body(), new TypeReference<ArrayList<LocationDTO>>() {});
    }
    public WeatherDTO getLocationByLonLat(Double latitude, Double longitude) throws URISyntaxException, IOException
            , InterruptedException, ApiKeyException, LocationInfoException, BadRequestException, CallsPerMinuteException {
        HttpRequest request = HttpRequest.newBuilder(new URI(URL_FIND_LOCATION_BY_LOT_LAN
                + "?lat=" + latitude
                + "&lon=" + longitude
                + "&appid=" + KEY)).GET().build();
        HttpClient client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        checkResponseCode(response);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.body(), WeatherDTO.class);
    }

    public void checkResponseCode(HttpResponse response) throws ApiKeyException, LocationInfoException
            ,CallsPerMinuteException, BadRequestException {
        int code = response.statusCode();
        if (code == 429) {
            log.error("> 60 API calls per minute");
            throw new CallsPerMinuteException("429 60 API calls per minute");
        }
        if (code == 404) {
            log.error("invalid location info");
            throw new LocationInfoException("404 invalid location info");
        }
        if (code == 401) {
            log.error("invalid api key");
            throw new ApiKeyException("401 invalid api key");
        }
        if (code == 400) {
            log.error("bad request");
            throw new BadRequestException("400 bad request");
        }
    }
    public Double conversionToCelsius(Double temperature) {
        BigDecimal value = BigDecimal.valueOf(273.15);
        BigDecimal decimalTemperature = BigDecimal.valueOf(temperature);
        return decimalTemperature.subtract(value).setScale(1, RoundingMode.HALF_DOWN).doubleValue();
    }
}
