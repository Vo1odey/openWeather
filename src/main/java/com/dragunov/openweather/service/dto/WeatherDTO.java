package com.dragunov.openweather.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherDTO {

    @JsonProperty("cod")
    private int code;

    @JsonProperty("name")
    private String name;

    @JsonProperty("coord")
    private Coordinates coordinates;

    @JsonProperty("weather")
    private List<Weather> weathers;

    @JsonProperty("main")
    private Temperature temperature;

    @JsonProperty("wind")
    private Wind wind;

    @JsonProperty("sys")
    private Country country;
}
