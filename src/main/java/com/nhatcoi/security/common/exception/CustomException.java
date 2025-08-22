package com.nhatcoi.security.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final Object[] args;
    
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessageKey()); // Fallback to message key
        this.errorCode = errorCode;
        this.args = new Object[0];
    }
    
    public CustomException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.args = new Object[0];
    }
    
    public CustomException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessageKey()); // Fallback to message key
        this.errorCode = errorCode;
        this.args = args;
    }
    
    public CustomException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
        this.args = new Object[0];
    }
    
    public CustomException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode.getMessageKey(), cause); // Fallback to message key
        this.errorCode = errorCode;
        this.args = args;
    }
}
