package com.example.kwordpocket.user.service;

import com.example.kwordpocket.auth.dto.AuthUser;
import com.example.kwordpocket.user.dto.UserDeleteRequest;
import com.example.kwordpocket.user.dto.UserGetResponse;
import com.example.kwordpocket.user.dto.UserUpdateRequest;
import com.example.kwordpocket.user.entity.User;
import com.example.kwordpocket.user.exception.PasswordNotMatchException;
import com.example.kwordpocket.user.exception.UserNotFoundException;
import com.example.kwordpocket.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserGetResponse> getAll() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> new UserGetResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getRole()
                )).toList();
    }

    @Transactional(readOnly = true)
    public UserGetResponse getOne(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException()
        );
        return new UserGetResponse(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }

    @Transactional
    public void updatePassword(AuthUser authUser, UserUpdateRequest request) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(
                () -> new UserNotFoundException()
        );
        String oldEncodedPassword = user.getPassword();
        String oldRawPassword = request.getOldPassword();
        boolean matches = passwordEncoder.matches(oldRawPassword, oldEncodedPassword);

        if (!matches) {
            throw new PasswordNotMatchException();
        }

        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.updatePassword(encodedNewPassword);
    }

    @Transactional
    public void deleteMe(AuthUser authUser, UserDeleteRequest request) {
        User user = userRepository.findById(authUser.getId()).orElseThrow(
                UserNotFoundException::new
        );

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new PasswordNotMatchException();
        }

        userRepository.delete(user);
    }
}