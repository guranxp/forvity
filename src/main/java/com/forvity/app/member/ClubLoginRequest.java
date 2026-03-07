package com.forvity.app.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClubLoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {}