package com.nhatcoi.security.auth.service;

import com.nhatcoi.security.security.jwt.JwtService;
import com.nhatcoi.security.auth.dto.request.AuthenticationRequest;
import com.nhatcoi.security.auth.dto.response.AuthenticationResponse;
import com.nhatcoi.security.auth.dto.request.LogoutRequest;
import com.nhatcoi.security.auth.dto.response.RefreshTokenRequest;
import com.nhatcoi.security.auth.dto.request.RegisterRequest;
import com.nhatcoi.security.common.exception.AuthenticationException;
import com.nhatcoi.security.common.exception.ErrorCode;
import com.nhatcoi.security.auth.entity.Role;
import com.nhatcoi.security.auth.entity.User;
import com.nhatcoi.security.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthenticationException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        log.info("Đăng ký thành công cho người dùng: {}", request.getEmail());

        var jwtToken = jwtService.generateToken(user);
        var refreshTokenValue = refreshTokenService.createRefreshToken(user, getUserAgent(), getIpAddress());

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshTokenValue)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException(ErrorCode.USER_NOT_FOUND));

        var jwtToken = jwtService.generateToken(user);
        var refreshTokenValue = refreshTokenService.createRefreshToken(user, getUserAgent(), getIpAddress());

        log.info("Đăng nhập thành công cho người dùng: {}", request.getEmail());

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshTokenValue)
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        var refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new AuthenticationException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        refreshTokenService.verifyExpiration(refreshToken);

        var user = refreshToken.getUser();
        var jwtToken = jwtService.generateToken(user);
        
        // Rotate refresh token
        var newRefreshToken = refreshTokenService.rotateRefreshToken(
                request.getRefreshToken(), 
                getUserAgent(), 
                getIpAddress()
        );

        log.info("Refresh token thành công cho người dùng: {}", user.getEmail());

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public void logout(LogoutRequest request) {
        refreshTokenService.revokeRefreshToken(request.getRefreshToken());
        log.info("Đăng xuất thành công");
    }

    public void logoutAll(String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.USER_NOT_FOUND));
        refreshTokenService.revokeAllUserTokens(user);
        log.info("Đăng xuất tất cả sessions cho người dùng: {}", userEmail);
    }

    private String getUserAgent() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("User-Agent");
            }
        } catch (Exception e) {
            log.warn("Không thể lấy User-Agent: {}", e.getMessage());
        }
        return "Unknown";
    }

    private String getIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.warn("Không thể lấy IP address: {}", e.getMessage());
        }
        return "Unknown";
    }


}
