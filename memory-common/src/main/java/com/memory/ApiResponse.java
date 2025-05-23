package com.memory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private Integer statusCode;
    private String message;
    private T data;

    public static final ApiResponse<String> OK = new ApiResponse<>(200, "OK", null);

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "OK", data);
    }

    public static ApiResponse<Object> error(Integer statusCode, String message) {
        return new ApiResponse<>(statusCode, message, null);
    }

}
