package com.nhatcoi.security;

import com.nhatcoi.security.common.exception.AuthenticationException;
import com.nhatcoi.security.common.exception.ErrorCode;
import com.nhatcoi.security.common.exception.TokenException;
import com.nhatcoi.security.common.exception.ValidationException;
import com.nhatcoi.security.common.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageTest {

    private final MessageService messageService;

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
    public ResponseEntity<Map<String, String>> testSuccess() {
        Map<String, String> response = new HashMap<>();
        response.put("register", messageService.getMessage("success.register"));
        response.put("login", messageService.getMessage("success.login"));
        response.put("logout", messageService.getMessage("success.logout"));
        response.put("refresh", messageService.getMessage("success.refresh"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth-messages")
    public ResponseEntity<Map<String, String>> testAuthMessages() {
        Map<String, String> messages = new HashMap<>();
        messages.put("invalid_credentials", messageService.getMessage("auth.invalid.credentials"));
        messages.put("user_not_found", messageService.getMessage("auth.user.not.found"));
        messages.put("email_exists", messageService.getMessage("auth.email.exists"));
        messages.put("account_disabled", messageService.getMessage("auth.account.disabled"));
        messages.put("account_locked", messageService.getMessage("auth.account.locked"));
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/token-messages")
    public ResponseEntity<Map<String, String>> testTokenMessages() {
        Map<String, String> messages = new HashMap<>();
        messages.put("invalid", messageService.getMessage("token.invalid"));
        messages.put("expired", messageService.getMessage("token.expired"));
        messages.put("revoked", messageService.getMessage("token.revoked"));
        messages.put("refresh_not_found", messageService.getMessage("token.refresh.not.found"));
        messages.put("refresh_reuse", messageService.getMessage("token.refresh.reuse"));
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/validation-messages")
    public ResponseEntity<Map<String, String>> testValidationMessages() {
        Map<String, String> messages = new HashMap<>();
        messages.put("error", messageService.getMessage("validation.error"));
        messages.put("required_field", messageService.getMessage("validation.required.field", "email"));
        messages.put("invalid_email", messageService.getMessage("validation.invalid.email"));
        messages.put("password_weak", messageService.getMessage("validation.password.weak"));
        messages.put("password_length", messageService.getMessage("validation.password.length", 6, 20));
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/locale/{lang}")
    public ResponseEntity<Map<String, String>> testLocale(@PathVariable String lang) {
        Map<String, String> messages = new HashMap<>();
        messages.put("language", lang);
        messages.put("welcome", messageService.getMessageByLocale("success.login", lang));
        messages.put("error", messageService.getMessageByLocale("auth.invalid.credentials", lang));
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/parameters")
    public ResponseEntity<Map<String, String>> testParameters() {
        Map<String, String> messages = new HashMap<>();
        messages.put("session_limit", messageService.getMessage("session.limit.exceeded", 5));
        messages.put("password_length", messageService.getMessage("validation.password.length", 8, 16));
        messages.put("required_field", messageService.getMessage("validation.required.field", "password"));
        messages.put("role_required", messageService.getMessage("permission.role.required", "ADMIN"));
        return ResponseEntity.ok(messages);
    }
}
