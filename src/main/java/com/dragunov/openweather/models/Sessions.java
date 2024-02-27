package com.dragunov.openweather.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "sessions", schema = "public", catalog = "open_weather")
public class Sessions {

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne()
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "userid")
    private User user;

    @Column(name = "expiresat")
    private LocalDateTime expiresAt;

    public Sessions(String id, LocalDateTime expiresAt) {
        this.id = id;
        this.expiresAt = expiresAt;
    }
}
