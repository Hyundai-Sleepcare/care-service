package com.sleepcare.server.profile.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProfileCreateRequest(
        @NotBlank(message = NAME_LENGTH)
        @Size(max = NAME_MAX, message = NAME_LENGTH)
        String name,

        @NotNull(message = "필수 값입니다.")
        @Valid
        Preset preset) {

    public static final int NAME_MAX = 20;
    static final String NAME_LENGTH = "이름은 앞뒤 공백을 빼고 1~20자여야 합니다.";

    // 앞뒤 공백을 먼저 지우고 검사한다 (UI의 trim()과 같음)
    public ProfileCreateRequest {
        name = name == null ? null : name.strip();
    }
}
