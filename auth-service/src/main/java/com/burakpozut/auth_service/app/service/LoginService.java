package com.burakpozut.auth_service.app.service;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.auth_service.app.command.LoginCommand;
import com.burakpozut.auth_service.app.exception.InvalidCredentialsException;
import com.burakpozut.auth_service.domain.RefreshToken;
import com.burakpozut.auth_service.domain.RefreshTokenRepository;
import com.burakpozut.auth_service.domain.User;
import com.burakpozut.auth_service.domain.UserRepository;
import com.burakpozut.auth_service.infra.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public LoginResult handle(LoginCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(command.password(), user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        if (!user.enabled()) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtService.generateAccessToken(user.id(), user.email());
        String refreshToken = jwtService.generateRefreshToken();
        Instant expiresAt = Instant.now().plusMillis(jwtService.getRefreshTokenExpiration());

        String tokenHash = passwordEncoder.encode(refreshToken);
        RefreshToken refreshTokenEntity = RefreshToken.createNew(user.id(), tokenHash, expiresAt);
        refreshTokenRepository.save(refreshTokenEntity, true);

        return new LoginResult(accessToken, refreshToken);
    }

    public record LoginResult(String accessToken, String refreshToken) {
    }
}