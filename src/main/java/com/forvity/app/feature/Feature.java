package com.forvity.app.feature;

import com.guranxp.featureflags.FlagType;

import java.time.LocalDate;

import static com.guranxp.featureflags.FlagType.RELEASE;

public enum Feature {

    REGISTRATION("feature.registration", LocalDate.of(2026, 3, 19)),
    ADMIN("feature.admin", LocalDate.of(2026, 3, 19), FlagType.OPERATIONAL);

    private final String key;
    private final LocalDate createdAt;
    private final FlagType type;

    Feature(final String key) {
        this(key, LocalDate.now(), RELEASE);
    }

    Feature(final String key, final LocalDate createdAt) {
        this(key, createdAt, RELEASE);
    }

    Feature(final String key, final LocalDate createdAt, final FlagType type) {
        this.key = key;
        this.createdAt = createdAt;
        this.type = type;
    }

    public String key() {
        return key;
    }

    public LocalDate createdAt() {
        return createdAt;
    }

    public FlagType type() {
        return type;
    }
}
