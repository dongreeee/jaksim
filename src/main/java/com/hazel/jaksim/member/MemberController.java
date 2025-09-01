package com.hazel.jaksim.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/join")
    public String join(){ return "join.html"; }

    @PostMapping("/joinMember")
    public String joinMember(String username,
                             String password,
                             String displayname,
                             Model model){
        try{
            memberService.registerMember(username, password, displayname);
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
    public String login(){ return "login.html"; }

//    1. 로그인 페이지만들기
//    2. 폼으로 로그인하겠다고 설정
//    3. DB에 있던 유저 정보 꺼내는 코드

    @GetMapping("/mypage")
    public String mypage(Authentication auth){
        CustomUser result = (CustomUser) auth.getPrincipal();
        return "mypage.html";
    }

    @GetMapping("/pwchk")
    public String pwchk(Authentication auth){
//      instanceof : 이 객체가 특정 클래스 타입인지 확인 / OAuth2 로그인 사용자인지 판별하는 용도
        if(auth instanceof OAuth2AuthenticationToken){
            return "mypage.html";
        }
        return "pw_chk_mypage.html";
    }



    @PostMapping("/matchpw")
    public String matchpw(Authentication auth, String password){

        boolean bl_check = memberService.matchPwChk(auth, password);

        if(bl_check){
            return "mypage.html";
        }else{
            return "redirect:/pwchk";
        }
    }
}


