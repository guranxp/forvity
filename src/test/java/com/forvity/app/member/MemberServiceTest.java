package com.forvity.app.member;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final UUID clubId = UUID.randomUUID();

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberRepository, passwordEncoder, new SimpleMeterRegistry());
    }

    @Test
    void shouldSaveMemberWhenValidInput() {
        when(memberRepository.existsByEmailAndClubId("john@example.com", clubId)).thenReturn(false);
        when(memberRepository.existsByUsernameAndClubId("john", clubId)).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashed");
        when(memberRepository.save(any(Member.class))).thenAnswer(i -> i.getArgument(0));

        final var member = memberService.register(clubId, "john@example.com", "john", "secret");

        assertThat(member.getEmail()).isEqualTo("john@example.com");
        assertThat(member.getUsername()).isEqualTo("john");
        assertThat(member.getPassword()).isEqualTo("hashed");
        assertThat(member.getRoles()).containsExactly(MemberRoleType.MEMBER);
    }

    @Test
    void shouldThrowWhenEmailAlreadyInUse() {
        when(memberRepository.existsByEmailAndClubId("john@example.com", clubId)).thenReturn(true);

        assertThatThrownBy(() -> memberService.register(clubId, "john@example.com", "john", "secret"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Email already in use");

        verify(memberRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUsernameAlreadyInUse() {
        when(memberRepository.existsByEmailAndClubId("john@example.com", clubId)).thenReturn(false);
        when(memberRepository.existsByUsernameAndClubId("john", clubId)).thenReturn(true);

        assertThatThrownBy(() -> memberService.register(clubId, "john@example.com", "john", "secret"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Username already in use");

        verify(memberRepository, never()).save(any());
    }
}