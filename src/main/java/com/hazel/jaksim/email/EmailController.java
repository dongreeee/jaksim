package com.hazel.jaksim.email;

import com.hazel.jaksim.calendar.Calendar;
import com.hazel.jaksim.member.Member;
import com.hazel.jaksim.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    private final EmailRepository emailRepository;

    private final MemberRepository memberRepository;

    @PostMapping("/send")
    public ResponseEntity<String>sendCode(@RequestParam String username) throws Exception {

        Optional<Member> result = memberRepository.findByUsername(username);
        if (result.isPresent()){
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)  // 409: 중복
                    .body("이미 존재하는 아이디입니다.");
        }


        String code = emailService.sendEmail(username);

        Email email = new Email();
        email.setUsername(username);
        email.setAuthCode(code);
        email.setCreatedAt(LocalDateTime.now());
        email.setExpiredAt(LocalDateTime.now().plusMinutes(5));
        emailRepository.save(email);

        return ResponseEntity.ok("인증번호 전송");
    }

    @PostMapping("/verify")
    public ResponseEntity<String>verify(@RequestParam String username, @RequestParam String code){
        boolean result = emailService.verifyCode(username,code);
        Optional<Email> optionalEmail = emailRepository.findTopByUsernameOrderByIdDesc(username);

        if(result){
            Email email = optionalEmail.get();
            email.setVerified("1");
            emailRepository.save(email);
        }

        return result ? ResponseEntity.ok("인증 성공") : ResponseEntity.badRequest().body("인증 실패");

    }

}
