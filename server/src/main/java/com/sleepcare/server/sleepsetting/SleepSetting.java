package com.sleepcare.server.sleepsetting;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "sleep_setting")
public class SleepSetting {

    @Id
    private String ownerId;

    private boolean temperatureEnabled;

    @Column(precision = 3, scale = 1)
    private BigDecimal temperatureCelsius;

    private String fanMode;

    private Instant createdAt;

    private Instant updatedAt;

    protected SleepSetting() {
    }

    public SleepSetting(String ownerId, boolean temperatureEnabled, BigDecimal temperatureCelsius,
                        String fanMode, Instant now) {
        this.ownerId = ownerId;
        this.temperatureEnabled = temperatureEnabled;
        this.temperatureCelsius = temperatureCelsius;
        this.fanMode = fanMode;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public String getOwnerId() { return ownerId; }
    public boolean isTemperatureEnabled() { return temperatureEnabled; }
    public BigDecimal getTemperatureCelsius() { return temperatureCelsius; }
    public String getFanMode() { return fanMode; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
