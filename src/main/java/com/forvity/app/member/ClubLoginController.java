package com.forvity.app.member;

import com.forvity.app.club.ClubService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static org.springframework.util.Assert.notNull;

@Slf4j
@RestController
@RequestMapping("/api/v1/clubs/{slug}/login")
public class ClubLoginController {

    private final MemberService memberService;
    private final ClubService clubService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextRepository securityContextRepository;

    public ClubLoginController(
            final MemberService memberService,
            final ClubService clubService,
            final PasswordEncoder passwordEncoder,
            final SecurityContextRepository securityContextRepository) {
        notNull(memberService, "memberService must not be null");
        notNull(clubService, "clubService must not be null");
        notNull(passwordEncoder, "passwordEncoder must not be null");
        notNull(securityContextRepository, "securityContextRepository must not be null");
        this.memberService = memberService;
        this.clubService = clubService;
        this.passwordEncoder = passwordEncoder;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping
    public ResponseEntity<Void> login(
            @PathVariable final String slug,
            @RequestBody @Valid final ClubLoginRequest request,
            final HttpServletRequest httpRequest,
            final HttpServletResponse httpResponse) {
        log.info("POST /api/v1/clubs/{}/login {}", slug, kv("email", request.email()));

        final var club = clubService.getBySlug(slug);

        final var userDetails = memberService.loadForAuthentication(club.getId(), request.email())
            .filter(details -> passwordEncoder.matches(request.password(), details.getPassword()))
            .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        final var auth = UsernamePasswordAuthenticationToken.authenticated(
                userDetails, null, userDetails.getAuthorities()
        );
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        log.info("Club login successful {} {}", kv("slug", slug), kv("email", request.email()));
        return ResponseEntity.ok().build();
    }
}