package com.nhatcoi.security.common.exception;

public class TokenException extends CustomException {
    
    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public TokenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public TokenException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
    
    public TokenException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
