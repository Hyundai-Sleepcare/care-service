package com.sleepcare.server.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileNameRequest(
        @NotBlank(message = ProfileCreateRequest.NAME_LENGTH)
        @Size(max = ProfileCreateRequest.NAME_MAX, message = ProfileCreateRequest.NAME_LENGTH)
        String name) {

    // 앞뒤 공백을 먼저 지우고 검사한다 (생성 요청과 같은 규칙)
    public ProfileNameRequest {
        name = name == null ? null : name.strip();
    }
}
