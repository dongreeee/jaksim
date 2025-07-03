package com.hazel.jaksim.email;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    private final EmailRepository emailRepository;

    public ResponseEntity<?>sendCode(@RequestParam String username) {

        return null;
    }
}
