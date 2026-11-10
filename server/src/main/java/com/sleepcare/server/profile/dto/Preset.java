package com.sleepcare.server.profile.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@HeatVentExclusive
public record Preset(
        @NotNull(message = Preset.REQUIRED) @Valid Backrest backrest,
        @NotNull(message = Preset.REQUIRED) @Valid SeatLevel seatHeat,
        @NotNull(message = Preset.REQUIRED) @Valid SeatLevel seatVent,
        @NotNull(message = Preset.REQUIRED) @Valid Temperature temperature,
        @NotNull(message = Preset.REQUIRED) @Valid AmbientLight ambientLight) {

    public static final int ANGLE_MIN = 100;
    public static final int ANGLE_MAX = 145;
    public static final int LEVEL_MIN = 0;
    public static final int LEVEL_MAX = 3;

    // 앱이 fieldErrors.reason을 화면에 그대로 보여 주므로 한국어로 쓴다
    static final String REQUIRED = "필수 값입니다.";
    static final String ANGLE_RANGE = "100~145 사이 정수여야 합니다.";
    static final String LEVEL_RANGE = "0~3 사이 정수여야 합니다.";
    static final String TEMPERATURE_RANGE = "16.0~32.0 사이(0.5 단위)여야 합니다.";

    public record Backrest(
            @NotNull(message = REQUIRED)
            @Min(value = ANGLE_MIN, message = ANGLE_RANGE)
            @Max(value = ANGLE_MAX, message = ANGLE_RANGE)
            Integer angle) {
    }

    public record SeatLevel(
            @NotNull(message = REQUIRED)
            @Min(value = LEVEL_MIN, message = LEVEL_RANGE)
            @Max(value = LEVEL_MAX, message = LEVEL_RANGE)
            Integer level) {
    }

    public record Temperature(
            @NotNull(message = REQUIRED)
            @DecimalMin(value = "16.0", message = TEMPERATURE_RANGE)
            @DecimalMax(value = "32.0", message = TEMPERATURE_RANGE)
            @HalfStep(message = TEMPERATURE_RANGE)
            BigDecimal targetCelsius) {
    }

    public record AmbientLight(
            @NotNull(message = REQUIRED)
            Mood mood) {
    }
}
