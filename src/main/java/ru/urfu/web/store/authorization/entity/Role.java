package ru.urfu.web.store.authorization.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    private Roles role;

    public String getAuthority() {
        return this.role.name();
    }
}
