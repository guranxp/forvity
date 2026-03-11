package com.forvity.app.system;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemAccountServiceTest {

    @Mock
    private SystemAccountRepository systemAccountRepository;

    @Mock
    private SystemRoleRepository systemRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private SystemAccountService systemAccountService;

    @BeforeEach
    void setUp() {
        systemAccountService = new SystemAccountService(systemAccountRepository, systemRoleRepository, passwordEncoder, new SimpleMeterRegistry());
    }

    @Test
    void shouldCreateRootAccountWhenValidInput() {
        when(systemAccountRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashed");
        when(systemAccountRepository.save(any(SystemAccount.class))).thenAnswer(i -> i.getArgument(0));
        when(systemRoleRepository.save(any(SystemRole.class))).thenAnswer(i -> i.getArgument(0));

        final var account = systemAccountService.createRootAccount("admin@example.com", "secret");

        assertThat(account.getEmail()).isEqualTo("admin@example.com");
        assertThat(account.getPassword()).isEqualTo("hashed");
        verify(systemRoleRepository).save(any(SystemRole.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyInUse() {
        when(systemAccountRepository.existsByEmail("admin@example.com")).thenReturn(true);

        assertThatThrownBy(() -> systemAccountService.createRootAccount("admin@example.com", "secret"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Email already in use");

        verify(systemAccountRepository, never()).save(any());
    }

    @Test
    void shouldCreateSuperAdminWhenValidInput() {
        when(systemAccountRepository.existsByEmail("super@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashed");
        when(systemAccountRepository.save(any(SystemAccount.class))).thenAnswer(i -> i.getArgument(0));
        when(systemRoleRepository.save(any(SystemRole.class))).thenAnswer(i -> i.getArgument(0));

        final var role = systemAccountService.createSuperAdmin("super@example.com", "secret");

        assertThat(role.getRole()).isEqualTo(SystemRoleType.SUPERADMIN);
        assertThat(role.getSystemAccount().getEmail()).isEqualTo("super@example.com");
        verify(systemRoleRepository).save(any(SystemRole.class));
    }

    @Test
    void shouldThrowWhenSuperAdminEmailAlreadyInUse() {
        when(systemAccountRepository.existsByEmail("super@example.com")).thenReturn(true);

        assertThatThrownBy(() -> systemAccountService.createSuperAdmin("super@example.com", "secret"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Email already in use");

        verify(systemAccountRepository, never()).save(any());
    }

    @Test
    void shouldRevokeSystemRoleWhenValid() {
        final var accountId = UUID.randomUUID();
        final var otherAccountId = UUID.randomUUID();
        final var account = mock(SystemAccount.class);
        when(account.getId()).thenReturn(otherAccountId);

        final var role = mock(SystemRole.class);
        when(role.getRole()).thenReturn(SystemRoleType.SUPERADMIN);
        when(role.getSystemAccount()).thenReturn(account);

        when(systemRoleRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(role));
        when(systemRoleRepository.countByRoleAndDeletedAtIsNull(SystemRoleType.SUPERADMIN)).thenReturn(2L);

        systemAccountService.revokeSystemRole(UUID.randomUUID(), accountId);

        verify(role).softDelete();
        verify(systemRoleRepository).save(role);
    }

    @Test
    void shouldThrowWhenRevokingRoot() {
        final var role = mock(SystemRole.class);
        when(role.getRole()).thenReturn(SystemRoleType.ROOT);
        when(systemRoleRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(role));

        assertThatThrownBy(() -> systemAccountService.revokeSystemRole(UUID.randomUUID(), UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("ROOT role cannot be revoked");
    }

    @Test
    void shouldThrowWhenRevokingSelf() {
        final var accountId = UUID.randomUUID();
        final var account = mock(SystemAccount.class);
        when(account.getId()).thenReturn(accountId);

        final var role = mock(SystemRole.class);
        when(role.getRole()).thenReturn(SystemRoleType.SUPERADMIN);
        when(role.getSystemAccount()).thenReturn(account);
        when(systemRoleRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(role));

        assertThatThrownBy(() -> systemAccountService.revokeSystemRole(UUID.randomUUID(), accountId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot revoke your own role");
    }

    @Test
    void shouldThrowWhenRevokingLastSuperAdmin() {
        final var accountId = UUID.randomUUID();
        final var otherAccountId = UUID.randomUUID();
        final var account = mock(SystemAccount.class);
        when(account.getId()).thenReturn(otherAccountId);

        final var role = mock(SystemRole.class);
        when(role.getRole()).thenReturn(SystemRoleType.SUPERADMIN);
        when(role.getSystemAccount()).thenReturn(account);
        when(systemRoleRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(role));
        when(systemRoleRepository.countByRoleAndDeletedAtIsNull(SystemRoleType.SUPERADMIN)).thenReturn(1L);

        assertThatThrownBy(() -> systemAccountService.revokeSystemRole(UUID.randomUUID(), accountId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot revoke the last SUPERADMIN");
    }

    @Test
    void shouldThrowWhenRoleNotFound() {
        when(systemRoleRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> systemAccountService.revokeSystemRole(UUID.randomUUID(), UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("System role not found");
    }
}