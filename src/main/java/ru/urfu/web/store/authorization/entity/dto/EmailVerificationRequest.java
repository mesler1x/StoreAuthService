package ru.urfu.web.store.authorization.entity.dto;

import jakarta.validation.constraints.Email;

public record EmailVerificationRequest(
        @Email
        String email,
        Integer code
) {
}
