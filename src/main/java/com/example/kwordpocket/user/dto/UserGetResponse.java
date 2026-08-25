package com.example.kwordpocket.user.dto;

import com.example.kwordpocket.user.enums.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserGetResponse {
    private final Long id;
    private final String email;
    private final Role role;
}
