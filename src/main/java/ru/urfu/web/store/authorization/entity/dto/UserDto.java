package ru.urfu.web.store.authorization.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.urfu.web.store.authorization.entity.Recipient;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private UUID userId;
    private String userMail;
    private Recipient recipient;
}
