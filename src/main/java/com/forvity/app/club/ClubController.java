package com.forvity.app.club;

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
@RequestMapping("/api/v1/clubs")
public class ClubController {

    private final ClubService clubService;

    public ClubController(final ClubService clubService) {
        this.clubService = clubService;
    }

    @PostMapping
    public ResponseEntity<ClubResponse> create(@RequestBody @Valid final CreateClubRequest request) {
        log.info("POST /api/v1/clubs", kv("name", request.name()), kv("slug", request.slug()));
        final var club = clubService.create(request.name(), request.slug());
        final var response = ClubResponse.from(club);
        return ResponseEntity.created(URI.create("/api/v1/clubs/" + response.slug())).body(response);
    }
}