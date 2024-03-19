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
@Table(name = "sessions", schema = "public", catalog = "open_weather", indexes = {@Index(name = "expire_at_index", columnList = "expires_at")})
public class Sessions {

    @Id
    private String id;

    @ManyToOne()
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public Sessions(String id, LocalDateTime expiresAt) {
        this.id = id;
        this.expiresAt = expiresAt;
    }
}
