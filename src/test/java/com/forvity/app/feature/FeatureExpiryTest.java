package com.forvity.app.feature;

import com.guranxp.featureflags.FlagType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureExpiryTest {

    private static final int MAX_AGE_DAYS = 90;
    private static final int WARN_DAYS = 30;

    @Test
    void shouldNotHaveExpiredReleaseFlags() {
        final LocalDate expiry = LocalDate.now().minusDays(MAX_AGE_DAYS);

        final List<Feature> expired = Arrays.stream(Feature.values())
                .filter(f -> f.type() == FlagType.RELEASE)
                .filter(f -> !f.createdAt().isAfter(expiry))
                .collect(Collectors.toList());

        assertThat(expired)
                .as("Release flags older than %d days — clean up or promote to OPERATIONAL: %s", MAX_AGE_DAYS, expired)
                .isEmpty();
    }

    @Test
    void shouldWarnAboutReleaseFlagsExpiringSoon() {
        final LocalDate threshold = LocalDate.now().minusDays(MAX_AGE_DAYS - WARN_DAYS);

        final List<Feature> expiringSoon = Arrays.stream(Feature.values())
                .filter(f -> f.type() == FlagType.RELEASE)
                .filter(f -> !f.createdAt().isAfter(threshold))
                .collect(Collectors.toList());

        assertThat(expiringSoon)
                .as("Release flags expiring within %d days — plan cleanup: %s", WARN_DAYS, expiringSoon)
                .isEmpty();
    }
}
