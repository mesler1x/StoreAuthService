package ru.urfu.web.store.authorization.entity.dto;

import jakarta.validation.constraints.Email;

public record RegisterRequest(
        @Email
        String email
) {
}
