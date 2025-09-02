package com.hazel.jaksim.member;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomUser extends User implements OAuth2User {
    public String displayName;
    public Long id;
    private Map<String, Object> attributes;
    public CustomUser(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities
    ){
        super(username, password, authorities);
    }

    // OAuth2용 attributes 포함 생성자
    public CustomUser(String username,
                      String password,
                      Collection<? extends GrantedAuthority> authorities,
                      Map<String, Object> attributes) {
        super(username, password, authorities);
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return getUsername();
    }

    public boolean isSocialLogin(){
//        provider가 null이면 일반 로그인, 값이 있으면 소셜 로그인
        return this.attributes != null && !this.attributes.isEmpty();
    }
}

