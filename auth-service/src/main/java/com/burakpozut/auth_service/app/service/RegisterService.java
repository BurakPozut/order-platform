package com.burakpozut.auth_service.app.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.auth_service.app.command.RegisterCommand;
import com.burakpozut.auth_service.app.exception.EmailAlreadyExistsException;
import com.burakpozut.auth_service.domain.User;
import com.burakpozut.auth_service.domain.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User handle(RegisterCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException(command.email());
        }

        String passwordHash = passwordEncoder.encode(command.password());
        User user = User.createNew(command.email(), passwordHash);
        return userRepository.save(user, true);
    }
}