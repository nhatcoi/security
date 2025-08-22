package com.nhatcoi.security.auth.controller;

import com.nhatcoi.security.auth.dto.request.AuthenticationRequest;
import com.nhatcoi.security.auth.dto.response.AuthenticationResponse;
import com.nhatcoi.security.auth.dto.request.LogoutRequest;
import com.nhatcoi.security.auth.dto.response.RefreshTokenRequest;
import com.nhatcoi.security.auth.dto.request.RegisterRequest;
import com.nhatcoi.security.auth.service.AuthenticationService;
import com.nhatcoi.security.common.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final MessageService messageService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        log.info("Yêu cầu đăng ký từ email: {}", request.getEmail());
        AuthenticationResponse response = authenticationService.register(request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", messageService.getMessage("success.register"));
        result.put("data", response);
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        log.info("Yêu cầu đăng nhập từ email: {}", request.getEmail());
        AuthenticationResponse response = authenticationService.authenticate(request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", messageService.getMessage("success.login"));
        result.put("data", response);
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Authentication service is running");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        log.info("Yêu cầu refresh token");
        AuthenticationResponse response = authenticationService.refreshToken(request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", messageService.getMessage("success.refresh"));
        result.put("data", response);
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        log.info("Yêu cầu đăng xuất");
        authenticationService.logout(request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", messageService.getMessage("success.logout"));
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Map<String, Object>> logoutAll() {
        String userEmail = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        
        log.info("Yêu cầu đăng xuất tất cả sessions cho user: {}", userEmail);
        authenticationService.logoutAll(userEmail);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", messageService.getMessage("success.logout.all"));
        
        return ResponseEntity.ok(result);
    }
}
