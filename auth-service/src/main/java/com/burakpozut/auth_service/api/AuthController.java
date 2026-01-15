package com.burakpozut.auth_service.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.auth_service.api.dto.request.LoginRequest;
import com.burakpozut.auth_service.api.dto.request.RefreshTokenRequest;
import com.burakpozut.auth_service.api.dto.request.RegisterRequest;
import com.burakpozut.auth_service.api.dto.response.AuthResponse;
import com.burakpozut.auth_service.app.service.LoginService;
import com.burakpozut.auth_service.app.service.RefreshTokenService;
import com.burakpozut.auth_service.app.service.RegisterService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final RegisterService registerService;
    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        var command = AuthMapper.toCommand(request);
        registerService.handle(command);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        var command = AuthMapper.toCommand(request);
        var result = loginService.handle(command);
        return ResponseEntity.ok(new AuthResponse(result.accessToken(), result.refreshToken())); // We should send
                                                                                                 // refresh token etc
                                                                                                 // via cookies for
                                                                                                 // using http only but
                                                                                                 // since this is not
                                                                                                 // that project I might
                                                                                                 // let it slide
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        var command = AuthMapper.toCommand(request);
        var result = refreshTokenService.handle(command);
        return ResponseEntity.ok(new AuthResponse(result.accessToken(), result.refreshToken()));
    }
}