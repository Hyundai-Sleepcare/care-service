package com.sleepcare.server.profile.dto;

import com.sleepcare.server.profile.Profile;
import java.time.Instant;

public record ProfileResponse(
        Long profileId,
        String name,
        Preset preset,
        Instant createdAt,
        Instant updatedAt) {

    public static ProfileResponse from(Profile profile) {
        Preset preset = new Preset(
                new Preset.Backrest(profile.getBackrestAngle()),
                new Preset.SeatLevel(profile.getSeatHeatLevel()),
                new Preset.SeatLevel(profile.getSeatVentLevel()),
                new Preset.Temperature(profile.getTemperatureCelsius()),
                new Preset.AmbientLight(Mood.valueOf(profile.getAmbientLightMood())));
        return new ProfileResponse(profile.getProfileId(), profile.getName(), preset,
                profile.getCreatedAt(), profile.getUpdatedAt());
    }
}
