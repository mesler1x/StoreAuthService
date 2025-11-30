package ru.urfu.web.store.authorization.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.urfu.web.store.authorization.entity.dto.EmailVerificationRequest;
import ru.urfu.web.store.authorization.entity.dto.LoginRequest;
import ru.urfu.web.store.authorization.service.AuthService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public void login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Получен запрос на login пользователя с email {}", loginRequest.email());
        authService.processLogin(loginRequest);
    }

    @PostMapping("/code/verify")
    public void verifyEmail(@Valid @RequestBody EmailVerificationRequest emailVerificationRequest) {
        log.info("Получен запрос на подтвердение почты пользователя с email {}", emailVerificationRequest.email());
        authService.processMailVerification(emailVerificationRequest);
    }

    @PostMapping("/code/resend")
    public void resendVerificationCode(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Получен запрос на переотправку кода подтверждения на почту {}", loginRequest.email());
        authService.processMailResend(loginRequest);
    }

}
