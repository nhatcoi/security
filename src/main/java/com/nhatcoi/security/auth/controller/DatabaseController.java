package com.nhatcoi.security.auth.controller;

import com.nhatcoi.security.auth.entity.RefreshToken;
import com.nhatcoi.security.auth.entity.User;
import com.nhatcoi.security.auth.repository.RefreshTokenRepository;
import com.nhatcoi.security.auth.repository.UserRepository;
import com.nhatcoi.security.common.dto.ResponseData;
import com.nhatcoi.security.common.service.ResponseDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/database")
@RequiredArgsConstructor
@Slf4j
public class DatabaseController {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ResponseDataService responseDataService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Object>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("totalUsers", users.size());
            userData.put("users", users.stream().map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("email", user.getEmail());
                userMap.put("firstName", user.getFirstName());
                userMap.put("lastName", user.getLastName());
                userMap.put("role", user.getRole());
                userMap.put("enabled", user.isEnabled());
                return userMap;
            }).toList());
            
            ResponseData<Object> result = ResponseData.success(userData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            ResponseData<Object> error = responseDataService.error("db.error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/refresh-tokens")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Object>> getAllRefreshTokens() {
        try {
            List<RefreshToken> tokens = refreshTokenRepository.findAll();
            
            Map<String, Object> tokenData = new HashMap<>();
            tokenData.put("totalTokens", tokens.size());
            tokenData.put("tokens", tokens.stream().map(token -> {
                Map<String, Object> tokenMap = new HashMap<>();
                tokenMap.put("id", token.getId());
                tokenMap.put("tokenHash", token.getTokenHash());
                tokenMap.put("familyId", token.getFamilyId());
                tokenMap.put("userAgent", token.getUserAgent());
                tokenMap.put("ipAddress", token.getIpAddress());
                tokenMap.put("expiresAt", token.getExpiresAt());
                tokenMap.put("revoked", token.isRevoked());
                tokenMap.put("replacedById", token.getReplacedById());
                tokenMap.put("createdAt", token.getCreatedAt());
                tokenMap.put("userEmail", token.getUser().getEmail());
                return tokenMap;
            }).toList());
            
            ResponseData<Object> result = ResponseData.success(tokenData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            ResponseData<Object> error = responseDataService.error("db.error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Object>> getDatabaseStats() {
        try {
            long userCount = userRepository.count();
            long tokenCount = refreshTokenRepository.count();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", userCount);
            stats.put("totalRefreshTokens", tokenCount);
            stats.put("databaseType", "H2 In-Memory");
            stats.put("jdbcUrl", "jdbc:h2:mem:testdb");
            
            ResponseData<Object> result = ResponseData.success(stats);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            ResponseData<Object> error = responseDataService.error("db.error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
