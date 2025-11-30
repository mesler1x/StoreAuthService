package ru.urfu.web.store.authorization.entity.dto;

import jakarta.validation.constraints.Email;

public record LoginRequest(
        @Email
        String email
) {
}
