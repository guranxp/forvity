package com.forvity.app.member;

import com.forvity.app.club.ClubService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@RestController
@RequestMapping("/api/v1/clubs/{slug}/members")
public class MemberController {

    private final MemberService memberService;
    private final ClubService clubService;

    public MemberController(final MemberService memberService, final ClubService clubService) {
        this.memberService = memberService;
        this.clubService = clubService;
    }

    @PostMapping
    public ResponseEntity<MemberResponse> register(@PathVariable final String slug, @RequestBody @Valid final RegisterMemberRequest request) {
        log.info("POST /api/v1/clubs/{}/members", slug, kv("email", request.email()), kv("username", request.username()));
        final var club = clubService.getBySlug(slug);
        final var member = memberService.register(club, request.email(), request.username(), request.password());
        final var response = MemberResponse.from(member);
        return ResponseEntity.created(URI.create("/api/v1/clubs/" + slug + "/members/" + response.id())).body(response);
    }
}