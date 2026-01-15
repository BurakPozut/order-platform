package com.burakpozut.auth_service.app.exception;

import com.burakpozut.common.exception.BusinessException;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}