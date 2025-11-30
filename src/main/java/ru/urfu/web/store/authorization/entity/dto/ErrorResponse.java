package ru.urfu.web.store.authorization.entity.dto;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        HttpStatus httpStatus,
        String message
) {
}
