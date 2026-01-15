package com.burakpozut.auth_service.app.exception;

import com.burakpozut.common.exception.BusinessException;

public class InvalidRefreshTokenException extends BusinessException {
    public InvalidRefreshTokenException() {
        super("Invalid or expired refresh token");
    }
}