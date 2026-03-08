package com.forvity.app.system;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static org.springframework.util.Assert.notNull;

@Slf4j
@RestController
@RequestMapping("/api/v1/login")
public class SystemLoginController {

    private final SystemAccountService systemAccountService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextRepository securityContextRepository;

    public SystemLoginController(
            final SystemAccountService systemAccountService,
            final PasswordEncoder passwordEncoder,
            final SecurityContextRepository securityContextRepository) {
        notNull(systemAccountService, "systemAccountService must not be null");
        notNull(passwordEncoder, "passwordEncoder must not be null");
        notNull(securityContextRepository, "securityContextRepository must not be null");
        this.systemAccountService = systemAccountService;
        this.passwordEncoder = passwordEncoder;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping
    public ResponseEntity<Void> login(
            @RequestBody @Valid final SystemLoginRequest request,
            final HttpServletRequest httpRequest,
            final HttpServletResponse httpResponse) {
        log.info("POST /api/v1/login", kv("email", request.email()));

        final var userDetails = systemAccountService.loadForAuthentication(request.email())
                .filter(details -> passwordEncoder.matches(request.password(), details.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        final var auth = UsernamePasswordAuthenticationToken.authenticated(
                userDetails, null, userDetails.getAuthorities());
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        log.info("System login successful", kv("email", request.email()));
        return ResponseEntity.ok().build();
    }
}