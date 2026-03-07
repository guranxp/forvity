package com.forvity.app.member;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<MemberResponse> register(@RequestBody @Valid final RegisterMemberRequest request) {
        log.info("POST /api/v1/members", kv("email", request.email()), kv("username", request.username()));
        final var member = memberService.register(request.email(), request.username(), request.password());
        final var response = MemberResponse.from(member);
        return ResponseEntity.created(URI.create("/api/v1/members/" + response.id())).body(response);
    }
}