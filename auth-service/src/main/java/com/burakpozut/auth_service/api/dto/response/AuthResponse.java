package com.burakpozut.auth_service.api.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken) {
}