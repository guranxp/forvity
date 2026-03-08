package com.forvity.app.member;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.Assert.state;

@Slf4j
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MeterRegistry meterRegistry;

    public MemberService(
            final MemberRepository memberRepository,
            final PasswordEncoder passwordEncoder,
            final MeterRegistry meterRegistry) {
        notNull(memberRepository, "memberRepository must not be null");
        notNull(passwordEncoder, "passwordEncoder must not be null");
        notNull(meterRegistry, "meterRegistry must not be null");
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.meterRegistry = meterRegistry;
    }

    public Member register(final UUID clubId, final String email, final String username, final String password) {
        log.info("Registering member {} {} {}", kv("clubId", clubId), kv("email", email), kv("username", username));
        notNull(clubId, "Club ID must not be null");
        hasText(email, "Email must not be blank");
        hasText(username, "Username must not be blank");
        hasText(password, "Password must not be blank");
        state(!memberRepository.existsByEmailAndClubId(email, clubId), "Email already in use");
        state(!memberRepository.existsByUsernameAndClubId(username, clubId), "Username already in use");

        final var member = new Member(
                clubId,
                email,
                username,
                passwordEncoder.encode(password),
                Set.of(MemberRoleType.MEMBER)
        );
        final var saved = memberRepository.save(member);

        meterRegistry.counter("members.registered").increment();
        log.info("Member registered {} {}", kv("memberId", saved.getId()), kv("username", saved.getUsername()));

        return saved;
    }

    public Optional<MemberDetails> loadForAuthentication(final UUID clubId, final String email) {
        notNull(clubId, "Club ID must not be null");
        hasText(email, "Email must not be blank");
        return memberRepository.findByEmailAndClubId(email, clubId)
            .map(MemberDetails::from);
    }
}