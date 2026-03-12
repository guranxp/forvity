package com.forvity.app.club;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static org.springframework.util.Assert.notNull;

@Slf4j
@RestController
@RequestMapping("/api/v1/clubs")
public class ClubController {

    private final ClubService clubService;

    public ClubController(final ClubService clubService) {
        notNull(clubService, "clubService must not be null");
        this.clubService = clubService;
    }

    @GetMapping
    public ResponseEntity<List<ClubResponse>> list() {
        log.info("GET /api/v1/clubs");
        final var clubs = clubService.listActive().stream().map(ClubResponse::from).toList();
        return ResponseEntity.ok(clubs);
    }

    @PostMapping
    public ResponseEntity<ClubResponse> create(@RequestBody @Valid final CreateClubRequest request) {
        log.info("POST /api/v1/clubs {} {}", kv("name", request.name()), kv("slug", request.slug()));
        final var club = clubService.create(request.name(), request.slug());
        final var response = ClubResponse.from(club);
        return ResponseEntity.created(URI.create("/api/v1/clubs/" + response.slug())).body(response);
    }
}