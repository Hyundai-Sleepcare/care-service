package com.sleepcare.server.profile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "profile")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    private String ownerId;

    private String name;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    private int backrestAngle;

    @JdbcTypeCode(SqlTypes.TINYINT)
    private int seatHeatLevel;

    @JdbcTypeCode(SqlTypes.TINYINT)
    private int seatVentLevel;

    @Column(precision = 3, scale = 1)
    private BigDecimal temperatureCelsius;

    private String ambientLightMood;

    private Instant createdAt;

    private Instant updatedAt;

    protected Profile() {
    }

    public Profile(String ownerId, String name, int backrestAngle,
                   int seatHeatLevel, int seatVentLevel,
                   BigDecimal temperatureCelsius, String ambientLightMood, Instant now) {
        this.ownerId = ownerId;
        this.name = name;
        this.backrestAngle = backrestAngle;
        this.seatHeatLevel = seatHeatLevel;
        this.seatVentLevel = seatVentLevel;
        this.temperatureCelsius = temperatureCelsius;
        this.ambientLightMood = ambientLightMood;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void rename(String name, Instant now) {
        this.name = name;
        this.updatedAt = now;
    }

    public void updatePreset(int backrestAngle, int seatHeatLevel, int seatVentLevel,
                             BigDecimal temperatureCelsius, String ambientLightMood, Instant now) {
        this.backrestAngle = backrestAngle;
        this.seatHeatLevel = seatHeatLevel;
        this.seatVentLevel = seatVentLevel;
        this.temperatureCelsius = temperatureCelsius;
        this.ambientLightMood = ambientLightMood;
        this.updatedAt = now;
    }

    public Long getProfileId() { return profileId; }
    public String getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public int getBackrestAngle() { return backrestAngle; }
    public int getSeatHeatLevel() { return seatHeatLevel; }
    public int getSeatVentLevel() { return seatVentLevel; }
    public BigDecimal getTemperatureCelsius() { return temperatureCelsius; }
    public String getAmbientLightMood() { return ambientLightMood; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
