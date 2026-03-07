package com.forvity.app.member;

import java.util.UUID;

public record MemberResponse(UUID id, String email, String username) {

    public static MemberResponse from(final Member member) {
        return new MemberResponse(member.getId(), member.getEmail(), member.getUsername());
    }
}