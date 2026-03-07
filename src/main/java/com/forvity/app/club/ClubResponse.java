package com.forvity.app.club;

import java.util.UUID;

public record ClubResponse(UUID id, String name, String slug) {

    public static ClubResponse from(final Club club) {
        return new ClubResponse(club.getId(), club.getName(), club.getSlug());
    }
}