package com.sleepcare.server.profile;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @JdbcTypeCode(SqlTypes.TINYINT)
    private int avatar;

    private boolean seatHeatEnabled;

    @JdbcTypeCode(SqlTypes.TINYINT)
    private int seatHeatLevel;

    private boolean seatVentEnabled;

    @JdbcTypeCode(SqlTypes.TINYINT)
    private int seatVentLevel;

    private boolean backrestEnabled;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    private int backrestAngle;

    private boolean ambientLightEnabled;

    private Instant createdAt;

    private Instant updatedAt;

    protected Profile() {
    }

    public Profile(String ownerId, String name, int avatar,
                   boolean seatHeatEnabled, int seatHeatLevel,
                   boolean seatVentEnabled, int seatVentLevel,
                   boolean backrestEnabled, int backrestAngle,
                   boolean ambientLightEnabled, Instant now) {
        this.ownerId = ownerId;
        this.name = name;
        this.avatar = avatar;
        this.seatHeatEnabled = seatHeatEnabled;
        this.seatHeatLevel = seatHeatLevel;
        this.seatVentEnabled = seatVentEnabled;
        this.seatVentLevel = seatVentLevel;
        this.backrestEnabled = backrestEnabled;
        this.backrestAngle = backrestAngle;
        this.ambientLightEnabled = ambientLightEnabled;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public Long getProfileId() { return profileId; }
    public String getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public int getAvatar() { return avatar; }
    public boolean isSeatHeatEnabled() { return seatHeatEnabled; }
    public int getSeatHeatLevel() { return seatHeatLevel; }
    public boolean isSeatVentEnabled() { return seatVentEnabled; }
    public int getSeatVentLevel() { return seatVentLevel; }
    public boolean isBackrestEnabled() { return backrestEnabled; }
    public int getBackrestAngle() { return backrestAngle; }
    public boolean isAmbientLightEnabled() { return ambientLightEnabled; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
