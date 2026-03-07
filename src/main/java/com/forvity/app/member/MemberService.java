package com.forvity.app.member;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.state;

@Slf4j
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MeterRegistry meterRegistry;

    public MemberService(final MemberRepository memberRepository, final PasswordEncoder passwordEncoder, final MeterRegistry meterRegistry) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.meterRegistry = meterRegistry;
    }

    public Member register(final String email, final String username, final String password) {
        log.info("Registering member", kv("email", email), kv("username", username));
        hasText(email, "Email must not be blank");
        hasText(username, "Username must not be blank");
        hasText(password, "Password must not be blank");
        state(!memberRepository.existsByEmail(email), "Email already in use");
        state(!memberRepository.existsByUsername(username), "Username already in use");

        final var member = new Member(email, username, passwordEncoder.encode(password));
        final var saved = memberRepository.save(member);

        meterRegistry.counter("members.registered").increment();
        log.info("Member registered", kv("memberId", saved.getId()), kv("username", saved.getUsername()));

        return saved;
    }
}
