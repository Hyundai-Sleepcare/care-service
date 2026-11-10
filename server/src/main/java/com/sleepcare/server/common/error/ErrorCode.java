package com.sleepcare.server.common.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "입력 값이 올바르지 않습니다."),
    OWNER_ID_REQUIRED(HttpStatus.UNAUTHORIZED, "X-Owner-Id 헤더가 없거나 UUID 형식이 아닙니다."),
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "프로필을 찾을 수 없습니다."),
    PROFILE_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "프로필은 최대 10개입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getMessage() { return message; }
}
