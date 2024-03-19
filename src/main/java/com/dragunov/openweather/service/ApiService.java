package com.dragunov.openweather.service;


import com.dragunov.openweather.exceptions.api.*;
import com.dragunov.openweather.models.Location;
import com.dragunov.openweather.models.User;
import com.dragunov.openweather.service.dto.WeatherDTO;
import com.dragunov.openweather.service.dto.LocationDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ApiService {
    private final String KEY = "18c261cc8f1e9cbcdf34e23e088dd57f";
    private final String URL_FIND_LOCATION_BY_NAME = "https://api.openweathermap.org/data/2.5/weather";
    private final String URL_FIND_LOCATIONS_BY_NAMES = "https://api.openweathermap.org/geo/1.0/direct";
    private final String URL_FIND_LOCATION_BY_LOT_LAN = "https://api.openweathermap.org/data/2.5/weather";

    public WeatherDTO getWeatherByName(String name) throws URISyntaxException, IOException
            , InterruptedException, ApiKeyException, LocationInfoException, BadRequestException, CallsPerMinuteException, LocationNotFoundException {
        Map <String, String> params = new HashMap<>();
        params.put("q", name);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(buildHttpClient(URL_FIND_LOCATION_BY_NAME, params, KEY), WeatherDTO.class);
    }

    public List<LocationDTO> getAvailableLocationNames(String name) throws URISyntaxException, IOException
            , InterruptedException, ApiKeyException, LocationInfoException, BadRequestException, CallsPerMinuteException, LocationNotFoundException {
        Map <String, String> params = new HashMap<>();
        params.put("q", name);
        params.put("limit", "5");
        ObjectMapper objectMapper = new ObjectMapper();
        List<LocationDTO> locationsDto = objectMapper.readValue(buildHttpClient(URL_FIND_LOCATIONS_BY_NAMES, params, KEY), new TypeReference<ArrayList<LocationDTO>>() {});
        if (locationsDto.isEmpty()) {
            throw new LocationNotFoundException("location not found");
        }
        return locationsDto;
    }

    public WeatherDTO getLocationByLonLat(Double latitude, Double longitude) throws URISyntaxException, IOException
            , InterruptedException, ApiKeyException, LocationInfoException, BadRequestException, CallsPerMinuteException, LocationNotFoundException {
        Map <String, String> params = new HashMap<>();
        params.put("lat", String.valueOf(latitude));
        params.put("lon", String.valueOf(longitude));
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(buildHttpClient(URL_FIND_LOCATION_BY_LOT_LAN, params, KEY), WeatherDTO.class);
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

    public String buildHttpClient(String baseURL, Map<String, String> params, String appid) throws URISyntaxException, IOException, InterruptedException, ApiKeyException, LocationInfoException, LocationNotFoundException {
        params.put("appid", appid);
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)).append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        String paramFromMap = builder.toString();
        URI uri = new URI(baseURL + "?" + paramFromMap);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        checkResponseCode(response);
        return response.body();
    }

    public Double conversionToCelsius(Double temperature) {
        BigDecimal value = BigDecimal.valueOf(273.15);
        BigDecimal decimalTemperature = BigDecimal.valueOf(temperature);
        return decimalTemperature.subtract(value).setScale(1, RoundingMode.HALF_DOWN).doubleValue();
    }

    public List<WeatherDTO> getAllUserWeatherToDisplay(User user) throws ApiKeyException, LocationInfoException, URISyntaxException, IOException, InterruptedException, LocationNotFoundException {
        List<Location> locations = user.getLocations();
        ArrayList<WeatherDTO> locationsWeathers = new ArrayList<>();
        for (Location location : locations) {
            String cityName = location.getName();
            WeatherDTO weatherDTO = getLocationByLonLat(location.getLatitude(), location.getLongitude());
            Double temperatureMax = weatherDTO.getTemperature().getTempMax();
            Double temperatureMin = weatherDTO.getTemperature().getTempMin();
            Double celsiusTemperatureMax = conversionToCelsius(temperatureMax);
            Double celsiusTemperatureMin = conversionToCelsius(temperatureMin);
            weatherDTO.getTemperature().setTempMax(celsiusTemperatureMax);
            weatherDTO.getTemperature().setTempMin(celsiusTemperatureMin);
            weatherDTO.setName(cityName);
            locationsWeathers.add(weatherDTO);
        }
        return locationsWeathers;
    }
}
