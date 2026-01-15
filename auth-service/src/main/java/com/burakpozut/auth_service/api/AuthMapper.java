package com.burakpozut.auth_service.api;

import com.burakpozut.auth_service.api.dto.request.LoginRequest;
import com.burakpozut.auth_service.api.dto.request.RefreshTokenRequest;
import com.burakpozut.auth_service.api.dto.request.RegisterRequest;
import com.burakpozut.auth_service.app.command.LoginCommand;
import com.burakpozut.auth_service.app.command.RefreshTokenCommand;
import com.burakpozut.auth_service.app.command.RegisterCommand;

public class AuthMapper {
    public static RegisterCommand toCommand(RegisterRequest request) {
        return new RegisterCommand(request.email(), request.password());
    }

    public static LoginCommand toCommand(LoginRequest request) {
        return new LoginCommand(request.email(), request.password());
    }

    public static RefreshTokenCommand toCommand(RefreshTokenRequest request) {
        return new RefreshTokenCommand(request.refreshToken());
    }
}