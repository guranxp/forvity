package com.forvity.app.member;

import java.util.Set;
import java.util.UUID;

public record MemberResponse(UUID id, String email, String username, Set<MemberRoleType> roles) {

    public static MemberResponse from(final Member member) {
        return new MemberResponse(member.getId(), member.getEmail(), member.getUsername(), member.getRoles());
    }
}