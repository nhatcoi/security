package com.nhatcoi.security.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData<T> {
    
    @Builder.Default
    private boolean success = true;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    private String message;
    
    private T data;
    
    private Object details;
    
    private String errorCode;
    
    private Integer code;
    
    // Method to set details and return this for chaining
    public ResponseData<T> setDetails(Object details) {
        this.details = details;
        return this;
    }
    
    // Method to set errorCode and return this for chaining
    public ResponseData<T> setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }
    
    // Method to set code and return this for chaining
    public ResponseData<T> setCode(Integer code) {
        this.code = code;
        return this;
    }
    
    // Static factory methods for success responses
    public static <T> ResponseData<T> success() {
        return ResponseData.<T>builder()
                .success(true)
                .build();
    }
    
    public static <T> ResponseData<T> success(T data) {
        return ResponseData.<T>builder()
                .success(true)
                .data(data)
                .build();
    }
    
    public static <T> ResponseData<T> success(String message, T data) {
        return ResponseData.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
    
    public static <T> ResponseData<T> success(String message) {
        return ResponseData.<T>builder()
                .success(true)
                .message(message)
                .build();
    }
    
    // Static factory methods for error responses
    public static <T> ResponseData<T> error(String message) {
        return ResponseData.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
    
    public static <T> ResponseData<T> error(String message, String errorCode) {
        return ResponseData.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }
    
    public static <T> ResponseData<T> error(String message, String errorCode, Integer code) {
        return ResponseData.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .code(code)
                .build();
    }
    
    public static <T> ResponseData<T> error(String message, Object details) {
        return ResponseData.<T>builder()
                .success(false)
                .message(message)
                .details(details)
                .build();
    }
    
    public static <T> ResponseData<T> error(String message, String errorCode, Object details) {
        return ResponseData.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .details(details)
                .build();
    }
}
