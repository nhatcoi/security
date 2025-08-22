package com.nhatcoi.security.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String errorCode;
    private int code;
    private Map<String, String> details;
    
    public static ErrorResponse fromErrorCode(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getHttpStatus().value())
                .error(errorCode.getHttpStatus().getReasonPhrase())
                .message(errorCode.getMessage())
                .errorCode(errorCode.getErrorCode())
                .code(errorCode.getCode())
                .build();
    }
    
    public static ErrorResponse fromErrorCode(ErrorCode errorCode, Object... args) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getHttpStatus().value())
                .error(errorCode.getHttpStatus().getReasonPhrase())
                .message(errorCode.getMessage(args))
                .errorCode(errorCode.getErrorCode())
                .code(errorCode.getCode())
                .build();
    }
    
    public static ErrorResponse fromErrorCode(ErrorCode errorCode, Map<String, String> details) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getHttpStatus().value())
                .error(errorCode.getHttpStatus().getReasonPhrase())
                .message(errorCode.getMessage())
                .errorCode(errorCode.getErrorCode())
                .code(errorCode.getCode())
                .details(details)
                .build();
    }
}
