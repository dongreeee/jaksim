package com.hazel.jaksim.email;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email, Long> {
    Optional<Email>findTopByUsernameOrderByIdDesc(String username);
}
