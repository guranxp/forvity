package com.forvity.app.club;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.Assert.state;

@Slf4j
@Service
public class ClubService {

    private final ClubRepository clubRepository;
    private final MeterRegistry meterRegistry;

    ClubService(final ClubRepository clubRepository, final MeterRegistry meterRegistry) {
        notNull(clubRepository, "clubRepository must not be null");
        notNull(meterRegistry, "meterRegistry must not be null");
        this.clubRepository = clubRepository;
        this.meterRegistry = meterRegistry;
    }

    public Club create(final String name, final String slug) {
        log.info("Creating club", kv("name", name), kv("slug", slug));
        hasText(name, "Name must not be blank");
        hasText(slug, "Slug must not be blank");
        state(!clubRepository.existsBySlug(slug), "Slug already in use");

        final var club = new Club(name, slug);
        final var saved = clubRepository.save(club);

        meterRegistry.counter("clubs.created").increment();
        log.info("Club created", kv("clubId", saved.getId()), kv("slug", saved.getSlug()));

        return saved;
    }

    public Club getBySlug(final String slug) {
        hasText(slug, "Slug must not be blank");
        return clubRepository.findBySlug(slug)
            .orElseThrow(() -> new NoSuchElementException("Club not found: " + slug));
    }
}