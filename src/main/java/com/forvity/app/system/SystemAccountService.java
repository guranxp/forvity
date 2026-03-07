package com.forvity.app.system;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.state;

@Slf4j
@Service
public class SystemAccountService {

    private final SystemAccountRepository systemAccountRepository;
    private final SystemRoleRepository systemRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MeterRegistry meterRegistry;

    public SystemAccountService(final SystemAccountRepository systemAccountRepository,
                                final SystemRoleRepository systemRoleRepository,
                                final PasswordEncoder passwordEncoder,
                                final MeterRegistry meterRegistry) {
        this.systemAccountRepository = systemAccountRepository;
        this.systemRoleRepository = systemRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.meterRegistry = meterRegistry;
    }

    public SystemAccount createRootAccount(final String email, final String password) {
        log.info("Creating ROOT system account", kv("email", email));
        hasText(email, "Email must not be blank");
        hasText(password, "Password must not be blank");
        state(!systemAccountRepository.existsByEmail(email), "Email already in use");

        final var username = email.substring(0, email.indexOf('@'));
        state(!systemAccountRepository.existsByUsername(username), "Username already in use");

        final var account = new SystemAccount(email, username, passwordEncoder.encode(password));
        final var saved = systemAccountRepository.save(account);

        final var role = new SystemRole(saved, SystemRoleType.ROOT);
        systemRoleRepository.save(role);

        meterRegistry.counter("system_accounts.created", "role", "ROOT").increment();
        log.info("ROOT system account created", kv("systemAccountId", saved.getId()));

        return saved;
    }

    public Optional<SystemAccountDetails> loadForAuthentication(final String email) {
        return systemAccountRepository.findByEmail(email)
                .map(account -> {
                    final var roles = systemRoleRepository.findAllBySystemAccountId(account.getId());
                    return SystemAccountDetails.from(account, roles);
                });
    }
}