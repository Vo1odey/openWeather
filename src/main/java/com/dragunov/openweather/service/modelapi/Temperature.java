package com.dragunov.openweather.service.modelapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Temperature {
    @JsonProperty("temp")
    Double temperature;
    @JsonProperty("temp_min")
    Double tempMin;
    @JsonProperty("temp_max")
    Double tempMax;
}
