package com.forvity.app.member;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member register(String email, String username, String password) {
        Assert.hasText(email, "Email must not be blank");
        Assert.hasText(username, "Username must not be blank");
        Assert.hasText(password, "Password must not be blank");
        Assert.state(!memberRepository.existsByEmail(email), "Email already in use");
        Assert.state(!memberRepository.existsByUsername(username), "Username already in use");

        Member member = new Member();
        member.setEmail(email);
        member.setUsername(username);
        member.setPassword(passwordEncoder.encode(password));

        return memberRepository.save(member);
    }
}