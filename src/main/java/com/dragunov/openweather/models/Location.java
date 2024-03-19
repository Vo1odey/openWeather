package com.dragunov.openweather.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "locations", schema = "public", catalog = "open_weather")
public class Location {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToOne()
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    public Location(String name, Double latitude, Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
