package com.memory.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerResponse<T> {

    private Integer statusCode;
    private String message;
    private T data;

    public static final ServerResponse<String> OK = new ServerResponse<>(200, "OK", null);

    public static <T> ServerResponse<T> success(T data) {
        return new ServerResponse<>(200, "OK", data);
    }

    public static ServerResponse<Object> error(Integer statusCode, String message) {
        return new ServerResponse<>(statusCode, message, null);
    }

}
