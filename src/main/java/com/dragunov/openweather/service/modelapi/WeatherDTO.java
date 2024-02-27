package com.dragunov.openweather.service.modelapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
    String name;
    @JsonProperty("coord")
    private Coordinates coordinates;
    @JsonProperty("main")
    private Temperature temperature;
    @JsonProperty("wind")
    private Wind wind;
}
