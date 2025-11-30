package ru.urfu.web.store.authorization.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.urfu.web.store.authorization.controller.exception.AlreadyExistException;
import ru.urfu.web.store.authorization.controller.exception.NotFoundException;
import ru.urfu.web.store.authorization.controller.exception.UnauthorizedException;
import ru.urfu.web.store.authorization.entity.dto.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistException(AlreadyExistException aex) {
        logError(aex);
        return new ErrorResponse(
                HttpStatus.CONFLICT,
                aex.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorized(UnauthorizedException uex) {
        logError(uex);
        return new ErrorResponse(
                HttpStatus.UNAUTHORIZED,
                uex.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException nfex) {
        logError(nfex);
        return new ErrorResponse(
                HttpStatus.NOT_FOUND,
                nfex.getMessage()
        );
    }

    public void logError(Exception exception) {
        log.info("Ошибка во время выполнения запроса с сообщением {}", exception.getMessage());
    }

}
