package com.hazel.jaksim.member;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerMember(String username,
                               String password,
                               String displayname) throws Exception {

        if (memberRepository.findByUsername(username).isPresent()){
            System.out.println("1");
            throw new Exception("존재하는아이디");
        }
        if (username.length() < 8 || password.length() < 8){
            System.out.println("2");
            throw new Exception("너무짧음");
        }

       // System.out.println("joinpw:"+password);

        var pw_encode = passwordEncoder.encode(password);
        //System.out.println("joinpw_encode:"+pw_encode);

        Member member = new Member();
        member.setUsername(username);
        member.setPassword(pw_encode);
        member.setDisplayname(displayname);

        memberRepository.save(member);
       // System.out.println("joinpw_encode2:"+pw_encode);

    }

    public boolean matchPwChk(Authentication auth, String password){
        boolean bl_check = false;
        if(auth != null && auth.isAuthenticated()){
            String username = auth.getName();
            System.out.println(username);
            Optional<Member> customUser = memberRepository.findByUsername(username);
            bl_check = passwordEncoder.matches(password, customUser.get().getPassword());
        }
        return bl_check;
    }

    public void updateMember(boolean socialLogin,
                               String username,
                               String password,
                               String rePassword,
                               String displayName) throws Exception {

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("존재하지 않는 사용자입니다."));


        member.setDisplayname(displayName);
        // 소셜 로그인일경우 비밀번호 체크 & 저장 x
        if(socialLogin){
        }else{
            // 비밀번호가 입력된 경우만 변경
            if (password != null && !password.isBlank()) {
                if (!password.equals(rePassword)) {
                    throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
                }

                if (password.length() < 8) {
                    throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
                }

                String pw_encode = passwordEncoder.encode(password);
                member.setPassword(pw_encode);
            }
            // 비밀번호 입력이 없으면 변경하지 않음 (이름만 수정)
        }

        memberRepository.save(member);

    }

}
