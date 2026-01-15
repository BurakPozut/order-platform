package com.burakpozut.auth_service.app.command;

public record RegisterCommand(
        String email,
        String password) {

}
