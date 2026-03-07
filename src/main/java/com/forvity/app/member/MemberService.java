package com.forvity.app.member;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member register(String email, String username, String password) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (memberRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already in use");
        }

        Member member = new Member();
        member.setEmail(email);
        member.setUsername(username);
        member.setPassword(passwordEncoder.encode(password));

        return memberRepository.save(member);
    }
}