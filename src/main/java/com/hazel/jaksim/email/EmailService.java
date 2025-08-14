package com.hazel.jaksim.email;

import com.hazel.jaksim.member.Member;
import com.hazel.jaksim.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Autowired
    private final JavaMailSender javaMailSender;

    private final MemberRepository memberRepository;
    private final EmailRepository emailRepository;
//
//    @PostConstruct
//    public void checkRepo() {
//        System.out.println("📌 EmailRepository 주입됨? " + (emailRepository != null));
//    }




    public ResponseEntity<String> sendVerificationCode(String username) throws Exception{

//      1. 회원 중복 확인
        if (memberRepository.findByUsername(username).isPresent()){
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)  // 409: 중복
                    .body("이미 존재하는 아이디입니다.");
        }
//      2. 메일 발송
        String code = sendEmail(username);

//      3. 인증 정보 저장
        Email email = new Email();
        email.setUsername(username);
        email.setAuthCode(code);
        email.setCreatedAt(LocalDateTime.now());
        email.setExpiredAt(LocalDateTime.now().plusMinutes(5));
        emailRepository.save(email);

        return ResponseEntity.ok("인증번호 전송");
    }

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

    public ResponseEntity<String>emailVerifyChk(String username, String code){
        boolean result = verifyCode(username,code);
        Optional<Email> optionalEmail = emailRepository.findTopByUsernameOrderByIdDesc(username);

        if(result){
            Email email = optionalEmail.get();
            email.setVerified("1");
            emailRepository.save(email);
        }

        return result ? ResponseEntity.ok("인증 성공") : ResponseEntity.badRequest().body("인증 실패");
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
