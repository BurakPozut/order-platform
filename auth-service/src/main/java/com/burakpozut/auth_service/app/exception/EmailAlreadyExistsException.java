package com.burakpozut.auth_service.app.exception;

import com.burakpozut.common.exception.BusinessException;

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
    }
}
