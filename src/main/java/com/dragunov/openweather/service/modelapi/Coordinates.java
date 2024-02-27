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
public class Coordinates {
    @JsonProperty("lon")
    Double longitude;
    @JsonProperty("lat")
    Double latitude;
}
