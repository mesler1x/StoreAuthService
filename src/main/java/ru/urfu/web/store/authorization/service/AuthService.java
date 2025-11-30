package ru.urfu.web.store.authorization.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.urfu.web.store.authorization.controller.exception.AlreadyExistException;
import ru.urfu.web.store.authorization.controller.exception.NotFoundException;
import ru.urfu.web.store.authorization.controller.exception.UnauthorizedException;
import ru.urfu.web.store.authorization.entity.dto.EmailVerificationRequest;
import ru.urfu.web.store.authorization.entity.dto.LoginRequest;
import ru.urfu.web.store.authorization.repository.RedisMailVerificationRepository;
import ru.urfu.web.store.authorization.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final MailSenderService mailSenderService;
    private final RedisMailVerificationRepository redisMailVerificationRepository;

    public void processLogin(@Valid LoginRequest loginRequest) {
        var email = loginRequest.email();
        if (userRepository.isUserEmailVerified(email)) {
            throw new AlreadyExistException("Пользователь с данным почтовым адресом уже существует");
        }

        if (!userRepository.isUserEmailExists(email)) {
            userRepository.save(email);
        }

        mailSenderService.processSendMailEvent(email);
    }

    public void processMailVerification(EmailVerificationRequest request) {
        redisMailVerificationRepository.get(request.email()).ifPresentOrElse(codeFromRedis -> {
            if (!codeFromRedis.equals(request.code())) {
                throw new UnauthorizedException("Код подтверждения введен не верно, повторите попытку");
            }

            userRepository.updateUserSetVerified(request.email());
        }, () -> {
            throw new UnauthorizedException("Превышено время ожинания ввода кода-подтвержения, переотправьте сообщение с кодом на почту повторно");
        });
    }

    public void processMailResend(LoginRequest loginRequest) {
        if (userRepository.isUserEmailVerified(loginRequest.email())) {
            throw new NotFoundException("Пользователь с данным почтовым уже подтвердил свою почту");
        }
        if (userRepository.isUserEmailExists(loginRequest.email())) {
            throw new NotFoundException("Пользователь с данным email не существует");
        }

        redisMailVerificationRepository.delete(loginRequest.email());
        mailSenderService.processSendMailEvent(loginRequest.email());

    }
}
