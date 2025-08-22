package com.nhatcoi.security.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // Authentication Errors (1000-1999)
    INVALID_CREDENTIALS(1000, "AUTH_001", "auth.invalid.credentials", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(1001, "AUTH_002", "auth.user.not.found", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS(1002, "AUTH_003", "auth.email.exists", HttpStatus.CONFLICT),
    ACCOUNT_DISABLED(1003, "AUTH_004", "auth.account.disabled", HttpStatus.FORBIDDEN),
    ACCOUNT_LOCKED(1004, "AUTH_005", "auth.account.locked", HttpStatus.FORBIDDEN),
    
    // Token Errors (2000-2999)
    INVALID_TOKEN(2000, "TOKEN_001", "token.invalid", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(2001, "TOKEN_002", "token.expired", HttpStatus.UNAUTHORIZED),
    TOKEN_REVOKED(2002, "TOKEN_003", "token.revoked", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NOT_FOUND(2003, "TOKEN_004", "token.refresh.not.found", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_REUSE_DETECTED(2004, "TOKEN_005", "token.refresh.reuse", HttpStatus.UNAUTHORIZED),
    TOKEN_FAMILY_REVOKED(2005, "TOKEN_006", "token.family.revoked", HttpStatus.UNAUTHORIZED),
    
    // Validation Errors (3000-3999)
    VALIDATION_ERROR(3000, "VAL_001", "validation.error", HttpStatus.BAD_REQUEST),
    REQUIRED_FIELD_MISSING(3001, "VAL_002", "validation.required.field", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT(3002, "VAL_003", "validation.invalid.email", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_WEAK(3003, "VAL_004", "validation.password.weak", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_LENGTH(3004, "VAL_005", "validation.password.length", HttpStatus.BAD_REQUEST),
    
    // Session Errors (4000-4999)
    SESSION_LIMIT_EXCEEDED(4000, "SESS_001", "session.limit.exceeded", HttpStatus.FORBIDDEN),
    SESSION_EXPIRED(4001, "SESS_002", "session.expired", HttpStatus.UNAUTHORIZED),
    INVALID_SESSION(4002, "SESS_003", "session.invalid", HttpStatus.UNAUTHORIZED),
    
    // Permission Errors (5000-5999)
    INSUFFICIENT_PERMISSIONS(5000, "PERM_001", "permission.insufficient", HttpStatus.FORBIDDEN),
    ACCESS_DENIED(5001, "PERM_002", "permission.access.denied", HttpStatus.FORBIDDEN),
    ROLE_REQUIRED(5002, "PERM_003", "permission.role.required", HttpStatus.FORBIDDEN),
    
    // Database Errors (6000-6999)
    DATABASE_ERROR(6000, "DB_001", "db.error", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_NOT_FOUND(6001, "DB_002", "db.data.not.found", HttpStatus.NOT_FOUND),
    DATA_CONFLICT(6002, "DB_003", "db.data.conflict", HttpStatus.CONFLICT),
    
    // System Errors (9000-9999)
    INTERNAL_SERVER_ERROR(9000, "SYS_001", "system.internal.error", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE(9001, "SYS_002", "system.service.unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    UNKNOWN_ERROR(9999, "SYS_999", "system.unknown.error", HttpStatus.INTERNAL_SERVER_ERROR);
    
    private final int code;
    private final String errorCode;
    private final String messageKey;  // Key để lookup trong messages_vi.properties
    private final HttpStatus httpStatus;
    
    /**
     * Lấy message key để lookup trong properties file
     */
    public String getMessageKey() {
        return this.messageKey;
    }
    
    /**
     * Fallback message khi không tìm thấy trong properties
     */
    public String getDefaultMessage() {
        return this.messageKey;
    }
    
    /**
     * Lấy message với parameters (deprecated - sử dụng MessageService thay thế)
     */
    @Deprecated
    public String getMessage(Object... args) {
        return String.format(this.messageKey, args);
    }
}
