package com.forvity.app.system;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
public class BootstrapService {

    private final SystemAccountService systemAccountService;
    private final SystemRoleRepository systemRoleRepository;

    @Value("${app.bootstrap.admin.email:}")
    private String bootstrapEmail;

    @Value("${app.bootstrap.admin.password:}")
    private String bootstrapPassword;

    BootstrapService(final SystemAccountService systemAccountService, final SystemRoleRepository systemRoleRepository) {
        this.systemAccountService = systemAccountService;
        this.systemRoleRepository = systemRoleRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void bootstrap() {
        if (!StringUtils.hasText(bootstrapEmail) || !StringUtils.hasText(bootstrapPassword)) {
            log.info("Bootstrap skipped — app.bootstrap.admin.email or app.bootstrap.admin.password not configured");
            return;
        }

        if (systemRoleRepository.existsByRole(SystemRoleType.ROOT)) {
            log.info("Bootstrap skipped — ROOT account already exists");
            return;
        }

        log.info("No ROOT account found — bootstrapping", kv("email", bootstrapEmail));
        systemAccountService.createRootAccount(bootstrapEmail, bootstrapPassword);
        log.info("Bootstrap complete", kv("email", bootstrapEmail));
    }
}