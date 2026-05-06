package com.example.FYP.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private int code;
    private String message;
    private T data;
    private String error;

    public static <T> ApiResponse<T> success(T data, int code, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(code)
                .message(message)
                .data(data)
                .error(null)
                .build();
    }

    public static <T> ApiResponse<T> error(String errorMessage, int code) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(errorMessage)
                .data(null)
                .error(errorMessage)
                .build();
    }
}
