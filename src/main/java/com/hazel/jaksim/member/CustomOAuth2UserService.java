package com.hazel.jaksim.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {


    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // google
        String providerId = oAuth2User.getAttribute("sub"); // 구글 고유 ID
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // DB에서 회원 조회
        Optional<Member> memberOptional = memberRepository.findByProviderAndProviderId(provider, providerId);
        Member member;

        if(memberOptional.isPresent()) {
            member = memberOptional.get(); // 기존 회원
        } else {
            // 신규 회원 생성
            Member newMember = new Member();
            newMember.setUsername(email);
            newMember.setDisplayname(name);
            newMember.setProvider(provider);
            newMember.setProviderId(providerId);
            memberRepository.save(newMember);
        }

        // Spring Security용 UserDetails 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                "sub"
        );
    }
}