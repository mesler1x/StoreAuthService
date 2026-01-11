package ru.urfu.web.store.authorization.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Recipient {
    private UUID id;
    private String description;
    private String firstName;
    private String middleName;
    private String lastName;
    private String address;
    private String phone;
}
