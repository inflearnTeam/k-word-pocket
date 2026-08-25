package com.example.kwordpocket.user.controller;

import com.example.kwordpocket.auth.dto.AuthUser;
import com.example.kwordpocket.user.dto.UserDeleteRequest;
import com.example.kwordpocket.user.dto.UserGetResponse;
import com.example.kwordpocket.user.dto.UserUpdateRequest;
import com.example.kwordpocket.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserGetResponse>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/users/me")
    public ResponseEntity<UserGetResponse> me(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return ResponseEntity.ok(userService.getOne(authUser.getId()));
    }

    @PutMapping("/users/me/password")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        userService.updatePassword(authUser, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/me")
    public ResponseEntity<Void> deleteMe(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UserDeleteRequest request
    ) {
        userService.deleteMe(authUser, request);
        return ResponseEntity.ok().build();
    }
}