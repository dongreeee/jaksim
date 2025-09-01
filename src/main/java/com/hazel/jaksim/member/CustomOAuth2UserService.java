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

        String provider = userRequest.getClientRegistration().getRegistrationId(); // google, kakao
        String providerId;
        String email;
        String name;

        if ("google".equals(provider)) {
            providerId = oAuth2User.getAttribute("sub");   // 구글 고유 ID
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");

        } else if ("kakao".equals(provider)) {
            providerId = String.valueOf(oAuth2User.getAttribute("id")); // 카카오 고유 ID

            Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
            email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

            Map<String, Object> profile = kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;
            name = profile != null ? (String) profile.get("nickname") : null;

        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 provider: " + provider);
        }

        // DB에서 회원 조회
        Optional<Member> memberOptional = memberRepository.findByProviderAndProviderId(provider, providerId);
        Member member;

        if (memberOptional.isPresent()) {
            member = memberOptional.get(); // 기존 회원
        } else {
            // 신규 회원 생성
            Member newMember = new Member();
            newMember.setUsername(email != null ? email : provider + "_" + providerId); // 이메일 없으면 providerId 대체
            newMember.setDisplayname(name);
            newMember.setProvider(provider);
            newMember.setProviderId(providerId);
            member = memberRepository.save(newMember);
        }

        // Spring Security용 UserDetails 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                "sub" // key 이름은 중요치 않음, attribute map에서 대표키 하나 지정
        );
    }
}