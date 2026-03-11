package com.forvity.app.system;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static org.springframework.util.Assert.notNull;

@Slf4j
@RestController
@RequestMapping("/api/v1/system/roles")
public class SystemRoleController {

    private final SystemAccountService systemAccountService;

    public SystemRoleController(final SystemAccountService systemAccountService) {
        notNull(systemAccountService, "systemAccountService must not be null");
        this.systemAccountService = systemAccountService;
    }

    @PostMapping
    public ResponseEntity<SystemRoleResponse> createSuperAdmin(
            @RequestBody @Valid final CreateSystemRoleRequest request) {
        log.info("POST /api/v1/system/roles {}", kv("email", request.email()));

        final var role = systemAccountService.createSuperAdmin(request.email(), request.password());
        final var response = SystemRoleResponse.from(role);

        final var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
