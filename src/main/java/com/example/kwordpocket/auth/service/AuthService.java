package com.example.kwordpocket.auth.service;

import com.example.kwordpocket.auth.dto.SigninRequest;
import com.example.kwordpocket.auth.dto.SignupRequest;
import com.example.kwordpocket.common.config.JwtUtil;
import com.example.kwordpocket.user.entity.User;
import com.example.kwordpocket.user.enums.Role;
import com.example.kwordpocket.user.exception.DuplicateEmailException;
import com.example.kwordpocket.user.exception.EmailNotFoundException;
import com.example.kwordpocket.user.exception.PasswordNotMatchException;
import com.example.kwordpocket.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException();
        }

        String encodePassword = passwordEncoder.encode(request.getPassword());
        Role role = (request.getRole() != null) ? Role.of(request.getRole()) : Role.ROLE_USER;

        User user = new User(
                request.getEmail(),
                encodePassword,
                role
        );

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public String signin(SigninRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new EmailNotFoundException()
        );

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new PasswordNotMatchException();
        }

        return jwtUtil.createToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }

}
