package com.forvity.app.system;

import java.util.UUID;

public record SystemRoleResponse(
        UUID id,
        String email,
        String role
) {

    static SystemRoleResponse from(final SystemRole systemRole) {
        return new SystemRoleResponse(
                systemRole.getId(),
                systemRole.getSystemAccount().getEmail(),
                systemRole.getRole().name()
        );
    }
}
