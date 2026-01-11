package ru.urfu.web.store.authorization.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipientDto {
    @NotNull
    private String userMail;
    private String description;
    private String firstName;
    private String middleName;
    private String lastName;
    private String address;
    private String phone;
}
