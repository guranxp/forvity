package com.forvity.app.system;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateSystemRoleRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
