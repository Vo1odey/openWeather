package com.dragunov.openweather.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Temperature {
    @JsonProperty("temp")
    double temperature;

    @JsonProperty("temp_min")
    double tempMin;

    @JsonProperty("temp_max")
    double tempMax;

    @JsonProperty("humidity")
    int humidity;
}
