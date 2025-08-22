package com.nhatcoi.security.auth.controller;

import com.nhatcoi.security.auth.dto.request.AuthenticationRequest;
import com.nhatcoi.security.auth.dto.response.AuthenticationResponse;
import com.nhatcoi.security.auth.dto.request.LogoutRequest;
import com.nhatcoi.security.auth.dto.response.RefreshTokenRequest;
import com.nhatcoi.security.auth.dto.request.RegisterRequest;
import com.nhatcoi.security.auth.service.AuthenticationService;
import com.nhatcoi.security.common.dto.ResponseData;
import com.nhatcoi.security.common.service.ResponseDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final ResponseDataService responseDataService;

    @PostMapping("/register")
    public ResponseEntity<ResponseData<AuthenticationResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        log.info("Yêu cầu đăng ký từ email: {}", request.getEmail());
        AuthenticationResponse response = authenticationService.register(request);
        
        ResponseData<AuthenticationResponse> result = responseDataService.success("success.register", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseData<AuthenticationResponse>> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        log.info("Yêu cầu đăng nhập từ email: {}", request.getEmail());
        AuthenticationResponse response = authenticationService.authenticate(request);
        
        ResponseData<AuthenticationResponse> result = responseDataService.success("success.login", response);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Authentication service is running");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseData<AuthenticationResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        log.info("Yêu cầu refresh token");
        AuthenticationResponse response = authenticationService.refreshToken(request);
        
        ResponseData<AuthenticationResponse> result = responseDataService.success("success.refresh", response);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseData<Object>> logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        log.info("Yêu cầu đăng xuất");
        authenticationService.logout(request);
        
        ResponseData<Object> result = responseDataService.success("success.logout");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout-all")
    public ResponseEntity<ResponseData<Object>> logoutAll() {
        String userEmail = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        
        log.info("Yêu cầu đăng xuất tất cả sessions cho user: {}", userEmail);
        authenticationService.logoutAll(userEmail);
        
        ResponseData<Object> result = responseDataService.success("success.logout.all");
        return ResponseEntity.ok(result);
    }
}
