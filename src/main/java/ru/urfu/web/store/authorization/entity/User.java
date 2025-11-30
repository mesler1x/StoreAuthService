package ru.urfu.web.store.authorization.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class User {
    private UUID id;
    private String email;
    private Boolean isVerified;

    public Collection<Role> getAuthorities() {
        return List.of(new Role(Roles.DEFAULT));
    }

    public String getPassword() {
        return "";
    }

    public String getUsername() {
        return this.email;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }
}
