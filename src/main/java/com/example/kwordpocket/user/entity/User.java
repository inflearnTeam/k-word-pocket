package com.example.kwordpocket.user.entity;

import com.example.kwordpocket.auth.dto.AuthUser;
import com.example.kwordpocket.common.entity.BaseEntity;
import com.example.kwordpocket.user.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    private User(Long id) {
        this.id = id;
    }

    public static User fromAuthUser(AuthUser authUser) {
        return new User(authUser.getId());
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
