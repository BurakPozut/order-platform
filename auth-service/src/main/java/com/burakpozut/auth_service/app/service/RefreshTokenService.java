package com.burakpozut.auth_service.app.service;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.auth_service.app.command.RefreshTokenCommand;
import com.burakpozut.auth_service.app.exception.InvalidRefreshTokenException;
import com.burakpozut.auth_service.domain.RefreshToken;
import com.burakpozut.auth_service.domain.RefreshTokenRepository;
import com.burakpozut.auth_service.domain.User;
import com.burakpozut.auth_service.domain.UserRepository;
import com.burakpozut.auth_service.infra.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public RefreshTokenResult handle(RefreshTokenCommand command) {
        RefreshToken existingToken = refreshTokenRepository.findByTokenHash(command.refreshToken())
                .orElseThrow(InvalidRefreshTokenException::new);

        if (!existingToken.isValid()) {
            throw new InvalidRefreshTokenException();
        }

        String tokenHash = passwordEncoder.encode(command.refreshToken());
        if (!passwordEncoder.matches(command.refreshToken(), existingToken.tokenHash())) {
            throw new InvalidRefreshTokenException();
        }

        User user = userRepository.findById(existingToken.userId())
                .orElseThrow(InvalidRefreshTokenException::new);

        if (!user.enabled()) {
            throw new InvalidRefreshTokenException();
        }

        refreshTokenRepository.deleteByUserId(user.id());

        String newAccessToken = jwtService.generateAccessToken(user.id(), user.email());
        String newRefreshToken = jwtService.generateRefreshToken();
        Instant expiresAt = Instant.now().plusMillis(jwtService.getRefreshTokenExpiration());

        String newTokenHash = passwordEncoder.encode(newRefreshToken);
        RefreshToken newRefreshTokenEntity = RefreshToken.createNew(user.id(), newTokenHash, expiresAt);
        refreshTokenRepository.save(newRefreshTokenEntity, true);

        return new RefreshTokenResult(newAccessToken, newRefreshToken);
    }

    public record RefreshTokenResult(String accessToken, String refreshToken) {
    }
}