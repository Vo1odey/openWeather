package com.dragunov.openweather.models;

import jakarta.persistence.*;
import lombok.*;


import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "users", schema = "public", catalog = "open_weather")

public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "login", unique = true, length = 6)
    private String login;

    @Column(name = "password", length = 6)
    private String password;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Location> locations;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sessions> sessions;
    public User(String login, String password) {
        this.login = login;
        this.password = password;
        locations = new ArrayList<>();
        sessions = new ArrayList<>();
    }
}
