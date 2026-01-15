package com.burakpozut.auth_service.app.command;

public record LoginCommand(
        String email,
        String password) {

}
