package com.hazel.jaksim.email;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String>sendCode(@RequestParam String username) throws Exception {
        return emailService.sendVerificationCode(username);
    }

    @PostMapping("/verify")
    public ResponseEntity<String>verify(@RequestParam String username, @RequestParam String code){
        return emailService.emailVerifyChk(username, code);
    }

}
