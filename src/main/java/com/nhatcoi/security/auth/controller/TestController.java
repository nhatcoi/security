package com.nhatcoi.security.auth.controller;

import com.nhatcoi.security.common.exception.AuthenticationException;
import com.nhatcoi.security.common.exception.ErrorCode;
import com.nhatcoi.security.common.exception.TokenException;
import com.nhatcoi.security.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    @GetMapping("/auth-error")
    public ResponseEntity<String> testAuthError() {
        throw new AuthenticationException(ErrorCode.INVALID_CREDENTIALS);
    }

    @GetMapping("/token-error")
    public ResponseEntity<String> testTokenError() {
        throw new TokenException(ErrorCode.TOKEN_EXPIRED);
    }

    @GetMapping("/validation-error")
    public ResponseEntity<String> testValidationError() {
        throw new ValidationException(ErrorCode.INVALID_EMAIL_FORMAT);
    }

    @GetMapping("/custom-message/{param}")
    public ResponseEntity<String> testCustomMessage(@PathVariable String param) {
        throw new AuthenticationException(ErrorCode.SESSION_LIMIT_EXCEEDED, param);
    }

    @GetMapping("/success")
    public ResponseEntity<String> testSuccess() {
        return ResponseEntity.ok("Test thành công!");
    }
}
