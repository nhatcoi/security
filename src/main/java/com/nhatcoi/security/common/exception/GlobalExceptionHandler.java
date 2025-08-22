package com.nhatcoi.security.common.exception;

import com.nhatcoi.security.common.dto.ResponseData;
import com.nhatcoi.security.common.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final MessageService messageService;
    
    public GlobalExceptionHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseData<Object>> handleCustomException(CustomException ex) {
        // Lấy message từ properties file
        String message = messageService.getMessage(ex.getErrorCode().getMessageKey(), ex.getArgs());
        
        ResponseData<Object> responseData = ResponseData.error(
            message,
            ex.getErrorCode().getErrorCode(),
            ex.getErrorCode().getCode()
        );
        
        log.error("Custom exception: {} - {}", ex.getErrorCode().getErrorCode(), message);
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(responseData);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseData<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            
            // Nếu error message là key, thì lookup trong properties
            if (errorMessage.startsWith("validation.") || errorMessage.startsWith("auth.")) {
                errorMessage = messageService.getMessage(errorMessage);
            }
            
            errors.put(fieldName, errorMessage);
        });

        ResponseData<Object> responseData = ResponseData.error(
            messageService.getMessage("validation.error"),
            "VAL_001",
            3000
        ).setDetails(errors);

        log.error("Validation error: {}", errors);
        return ResponseEntity.badRequest().body(responseData);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseData<Object>> handleBadCredentialsException(BadCredentialsException ex) {
        ResponseData<Object> responseData = ResponseData.error(
            messageService.getMessage("auth.invalid.credentials"),
            "AUTH_001",
            1000
        );

        log.error("Authentication error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseData<Object>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ResponseData<Object> responseData = ResponseData.error(
            messageService.getMessage("auth.user.not.found"),
            "AUTH_002",
            1001
        );

        log.error("User not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseData<Object>> handleRuntimeException(RuntimeException ex) {
        ResponseData<Object> responseData = ResponseData.error(
            ex.getMessage(),
            "RUNTIME_001",
            8000
        );

        log.error("Runtime error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(responseData);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData<Object>> handleGenericException(Exception ex) {
        ResponseData<Object> responseData = ResponseData.error(
            messageService.getMessage("system.internal.error"),
            "SYS_001",
            9000
        ).setDetails(ex.getMessage());

        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
    }
}
