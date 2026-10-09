package com.skillbridge.exception;

import java.time.Instant;
import java.util.List;

public record ApiError(int status, String code, String message, List<String> details, Instant timestamp) {

    public static ApiError of(int status, String code, String message, List<String> details) {
        return new ApiError(status, code, message, details, Instant.now());
    }
}
