package com.example.kwordpocket.user.enums;

import com.example.kwordpocket.user.exception.RoleNotMatchException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Role {

    ROLE_USER(Authority.USER),
    ROLE_ADMIN(Authority.ADMIN);

    private final String userRole;

    public static Role of(String role) {
        if (role == null || role.isBlank()) {
            return ROLE_USER;
        }

        String formatted = role.toUpperCase().startsWith("ROLE_")
                ? role.toUpperCase()
                : "ROLE_" + role.toUpperCase();

        return Arrays.stream(Role.values())
                .filter(value -> value.name().equals(formatted))
                .findFirst()
                .orElseThrow(RoleNotMatchException::new);
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}