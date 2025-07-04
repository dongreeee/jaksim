package com.hazel.jaksim.email;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Autowired
    private final JavaMailSender javaMailSender;

    private final EmailRepository emailRepository;
//
//    @PostConstruct
//    public void checkRepo() {
//        System.out.println("📌 EmailRepository 주입됨? " + (emailRepository != null));
//    }

    public String sendEmail(String toEmail){
        String authCode = generateCode();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("회원가입 인증번호");
        message.setText("인증번호는 " + authCode + " 입니다.");

        javaMailSender.send(message);
        return authCode;
    }

    private String generateCode(){
        Random rnd = new Random();
        int number = rnd.nextInt(888888) + 111111;
        return String.valueOf(number);
    }

    public boolean verifyCode(String username, String inputCode) {
        Email auth = emailRepository.findTopByUsernameOrderByIdDesc(username)
                .orElseThrow(() -> new RuntimeException("인증 요청 없음"));

        if (auth.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("인증번호 만료됨");
        }

        return auth.getAuthCode().equals(inputCode);
    }


}
