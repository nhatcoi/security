package com.nhatcoi.security.auth.service;

import com.nhatcoi.security.auth.entity.RefreshToken;
import com.nhatcoi.security.auth.entity.User;
import com.nhatcoi.security.common.exception.ErrorCode;
import com.nhatcoi.security.common.exception.TokenException;
import com.nhatcoi.security.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private Long refreshTokenDurationMs;

    @Value("${application.security.jwt.max-active-sessions:5}")
    private int maxActiveSessions;

    public String createRefreshToken(User user, String userAgent, String ipAddress) {
        // Kiểm tra và giới hạn số lượng sessions
        enforceSessionLimit(user);
        
        String familyId = UUID.randomUUID().toString();
        String tokenValue = UUID.randomUUID().toString();
        String tokenHash = hashToken(tokenValue);
        
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .familyId(familyId)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .expiresAt(Instant.now().plusMillis(refreshTokenDurationMs))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        log.info("Tạo refresh token mới cho user: {} với family: {}", user.getEmail(), familyId);
        
        return tokenValue;
    }

    public String rotateRefreshToken(String tokenValue, String userAgent, String ipAddress) {
        String tokenHash = hashToken(tokenValue);
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash);
        
        if (existingToken.isEmpty()) {
            throw new TokenException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        RefreshToken oldToken = existingToken.get();
        
        // Kiểm tra reuse detection
        if (oldToken.isRevoked()) {
            log.warn("Phát hiện reuse token! Revoking toàn bộ family: {}", oldToken.getFamilyId());
            refreshTokenRepository.revokeAllTokensInFamily(oldToken.getFamilyId());
            throw new TokenException(ErrorCode.REFRESH_TOKEN_REUSE_DETECTED);
        }

        // Tạo token mới trong cùng family
        String newTokenValue = UUID.randomUUID().toString();
        String newTokenHash = hashToken(newTokenValue);
        
        RefreshToken newToken = RefreshToken.builder()
                .user(oldToken.getUser())
                .tokenHash(newTokenHash)
                .familyId(oldToken.getFamilyId())
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .expiresAt(Instant.now().plusMillis(refreshTokenDurationMs))
                .revoked(false)
                .build();

        RefreshToken savedNewToken = refreshTokenRepository.save(newToken);
        
        // Revoke token cũ và set replacedById
        refreshTokenRepository.revokeTokenAndSetReplacedBy(oldToken.getId(), savedNewToken.getId());
        
        log.info("Rotate refresh token thành công cho user: {} family: {}", 
                oldToken.getUser().getEmail(), oldToken.getFamilyId());
        
        return newTokenValue;
    }

    public Optional<RefreshToken> findByToken(String tokenValue) {
        String tokenHash = hashToken(tokenValue);
        return refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenException(ErrorCode.TOKEN_EXPIRED);
        }

        if (token.isRevoked()) {
            throw new TokenException(ErrorCode.TOKEN_REVOKED);
        }

        return token;
    }

    @Transactional
    public void revokeRefreshToken(String tokenValue) {
        String tokenHash = hashToken(tokenValue);
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByTokenHash(tokenHash);
        if (refreshToken.isPresent()) {
            RefreshToken rt = refreshToken.get();
            refreshTokenRepository.revokeAllTokensInFamily(rt.getFamilyId());
            log.info("Đã thu hồi refresh token family cho user: {}", rt.getUser().getEmail());
        }
    }

    @Transactional
    public void revokeAllUserTokens(User user) {
        List<RefreshToken> activeTokens = refreshTokenRepository.findByUserAndRevokedFalse(user);
        for (RefreshToken token : activeTokens) {
            refreshTokenRepository.revokeAllTokensInFamily(token.getFamilyId());
        }
        log.info("Đã thu hồi tất cả refresh tokens cho user: {}", user.getEmail());
    }

    @Transactional
    public void revokeTokenFamily(String familyId) {
        refreshTokenRepository.revokeAllTokensInFamily(familyId);
        log.info("Đã thu hồi toàn bộ family: {}", familyId);
    }

    private void enforceSessionLimit(User user) {
        long activeSessionCount = refreshTokenRepository.countActiveTokensByUser(user);
        
        if (activeSessionCount >= maxActiveSessions) {
            // Revoke session cũ nhất (LRU)
            List<RefreshToken> oldestTokens = refreshTokenRepository
                    .findActiveTokensByUserOrderByCreatedAt(user);
            
            if (!oldestTokens.isEmpty()) {
                RefreshToken oldestToken = oldestTokens.get(0);
                refreshTokenRepository.revokeAllTokensInFamily(oldestToken.getFamilyId());
                log.info("Đã revoke session cũ nhất cho user: {} do vượt quá giới hạn", user.getEmail());
            }
        }
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new TokenException(ErrorCode.INTERNAL_SERVER_ERROR, "Không thể hash token", e);
        }
    }

    public List<RefreshToken> getUserActiveSessions(User user) {
        return refreshTokenRepository.findByUserAndRevokedFalse(user);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(Instant.now());
        log.info("Đã cleanup expired tokens");
    }
}
