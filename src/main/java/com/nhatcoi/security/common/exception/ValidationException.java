package com.nhatcoi.security.common.exception;

public class ValidationException extends CustomException {
    
    public ValidationException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public ValidationException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
    
    public ValidationException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
