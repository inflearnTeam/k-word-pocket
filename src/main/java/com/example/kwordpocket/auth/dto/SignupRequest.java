package com.example.kwordpocket.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequest {

    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    //role 입력 시 admin, 공란은 user
    private String role;
}
