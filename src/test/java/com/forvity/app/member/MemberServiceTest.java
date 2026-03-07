package com.forvity.app.member;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    void shouldSaveMemberWhenValidInput() {
        when(memberRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(memberRepository.existsByUsername("john")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashed");
        when(memberRepository.save(any(Member.class))).thenAnswer(i -> i.getArgument(0));

        Member member = memberService.register("john@example.com", "john", "secret");

        assertThat(member.getEmail()).isEqualTo("john@example.com");
        assertThat(member.getUsername()).isEqualTo("john");
        assertThat(member.getPassword()).isEqualTo("hashed");
    }

    @Test
    void shouldThrowWhenEmailAlreadyInUse() {
        when(memberRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> memberService.register("john@example.com", "john", "secret"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Email already in use");

        verify(memberRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUsernameAlreadyInUse() {
        when(memberRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(memberRepository.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> memberService.register("john@example.com", "john", "secret"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Username already in use");

        verify(memberRepository, never()).save(any());
    }
}