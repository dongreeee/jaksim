package com.hazel.jaksim.member;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/join")
    public String join(){

        return "join.html";
    }

    @GetMapping("/success")
    @ResponseBody
    public String success(Authentication auth){
       String a = "";
        if(auth != null && auth.isAuthenticated()){
            a = "나와";
        }

        return a;
    }

    @PostMapping("/joinMember")
    public String joinMember(String username,
                             String password,
                             String displayname,
                             Model model){
        try{
            Optional<Member> result = memberRepository.findByUsername(username);
            if (result.isPresent()){
                System.out.println("1");
                throw new Exception("존재하는아이디");
            }
            if (username.length() < 8 || password.length() < 8){
                System.out.println("2");
                throw new Exception("너무짧음");
            }


            var pw_encode = passwordEncoder.encode(password);


            Member member = new Member();
            member.setUsername(username);
            member.setPassword(pw_encode);
            member.setDisplayname(displayname);

            memberRepository.save(member);
            return "redirect:/login";
        }
        catch (Exception e){
            String msg = switch (e.getMessage()){
                case "존재하는아이디" -> "이미 존재하는 아이디입니다.";
                case "너무짧음" -> "아이디와 비밀번호는 8자 이상이어야 합니다.";
                default -> "회원가입 중 오류가 발생했습니다.";
            };
            model.addAttribute("errorMsg", msg);
            return "join";

        }
    }

    @GetMapping("/login")
    public String login(){

        return "login.html";
    }

//    1. 로그인 페이지만들기
//    2. 폼으로 로그인하겠다고 설정
//    3. DB에 있던 유저 정보 꺼내는 코드

    @GetMapping("/mypage")
    public String mypage(Authentication auth){

        CustomUser result = (CustomUser) auth.getPrincipal();
        return "mypage.html";
    }

    @GetMapping("/pwchk")
    public String pwchk(){
        return "pw_chk_mypage.html";
    }

    @GetMapping("/calendar")
    public String calendar(){
        return  "calendar.html";
    }

    @PostMapping("/matchpw")
    public String matchpw(Authentication auth, String password){

        Boolean bl_check = false;
        if(auth != null && auth.isAuthenticated()){
            String username = auth.getName();
            System.out.println(username);
            Optional<Member> customUser = memberRepository.findByUsername(username);
            bl_check = passwordEncoder.matches(password, customUser.get().getPassword());
        }

        if(bl_check){
            return "mypage.html";
        }else{
            return "redirect:/pwchk";
        }
    }
}


