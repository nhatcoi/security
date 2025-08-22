package com.nhatcoi.security.common.exception;

public class AuthenticationException extends CustomException {
    
    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public AuthenticationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public AuthenticationException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
    
    public AuthenticationException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
