package com.nhatcoi.security.auth.controller;

import com.nhatcoi.security.auth.entity.RefreshToken;
import com.nhatcoi.security.auth.entity.User;
import com.nhatcoi.security.auth.repository.UserRepository;
import com.nhatcoi.security.auth.service.RefreshTokenService;
import com.nhatcoi.security.common.exception.AuthenticationException;
import com.nhatcoi.security.common.exception.ErrorCode;
import com.nhatcoi.security.common.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Slf4j
public class SessionController {

    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final MessageService messageService;

    @GetMapping("/my-sessions")
    public ResponseEntity<Map<String, Object>> getMySessions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.USER_NOT_FOUND));

        List<RefreshToken> sessions = refreshTokenService.getUserActiveSessions(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalSessions", sessions.size());
        response.put("sessions", sessions.stream().map(session -> {
            Map<String, Object> sessionMap = new HashMap<>();
            sessionMap.put("id", session.getId());
            sessionMap.put("familyId", session.getFamilyId());
            sessionMap.put("userAgent", session.getUserAgent());
            sessionMap.put("ipAddress", session.getIpAddress());
            sessionMap.put("createdAt", session.getCreatedAt());
            sessionMap.put("expiresAt", session.getExpiresAt());
            sessionMap.put("revoked", session.isRevoked());
            return sessionMap;
        }).toList());
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/revoke-family/{familyId}")
    public ResponseEntity<Map<String, Object>> revokeSessionFamily(@PathVariable String familyId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.USER_NOT_FOUND));

        // Kiểm tra xem family có thuộc về user này không
        List<RefreshToken> userSessions = refreshTokenService.getUserActiveSessions(user);
        boolean isOwner = userSessions.stream()
                .anyMatch(session -> session.getFamilyId().equals(familyId));

        if (!isOwner) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", messageService.getMessage("permission.access.denied"));
            return ResponseEntity.badRequest().body(error);
        }

        refreshTokenService.revokeTokenFamily(familyId);
        log.info("User {} đã thu hồi session family: {}", userEmail, familyId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", messageService.getMessage("success.session.revoke"));
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/revoke-all")
    public ResponseEntity<Map<String, Object>> revokeAllMySessions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.USER_NOT_FOUND));

        refreshTokenService.revokeAllUserTokens(user);
        log.info("User {} đã thu hồi tất cả sessions", userEmail);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", messageService.getMessage("success.session.revoke.all"));
        
        return ResponseEntity.ok(response);
    }
}
