package com.forvity.app.member;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.state;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(final MemberRepository memberRepository, final PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member register(final String email, final String username, final String password) {
        hasText(email, "Email must not be blank");
        hasText(username, "Username must not be blank");
        hasText(password, "Password must not be blank");
        state(!memberRepository.existsByEmail(email), "Email already in use");
        state(!memberRepository.existsByUsername(username), "Username already in use");

        final var member = new Member(email, username, passwordEncoder.encode(password));

        return memberRepository.save(member);
    }
}
