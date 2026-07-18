package com.medisalud.appointment.domain.wrapper;

import java.util.Collections;
import java.util.List;

public record ApiResponse<T>(
    boolean isSuccess,
    String message,
    List<String> errors,
    T data
) {
    public ApiResponse {
        if (errors == null) {
            errors = Collections.emptyList();
        }
    }

    public static <T> ApiResponse<T> failed(String message, List<String> errors) {
        return new ApiResponse<>(
            false,
            message,
            errors != null ? errors : Collections.emptyList(),
            null
        );
    }

    public static <T> ApiResponse<T> failed(String message) {
        return failed(message, Collections.emptyList());
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, Collections.emptyList(), data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation completed successfully.");
    }
}
