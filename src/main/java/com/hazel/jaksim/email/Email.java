package com.hazel.jaksim.email;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_auth")
@ToString
@Getter
@Setter
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(name = "auth_code", nullable = false, length = 10)
    private String authCode;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    private String verified;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
