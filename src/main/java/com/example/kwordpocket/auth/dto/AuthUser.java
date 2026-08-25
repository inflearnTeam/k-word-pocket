package com.example.kwordpocket.auth.dto;

import com.example.kwordpocket.user.enums.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthUser {

    private final Long id;
    private final String email;
    private final Role role;

}
