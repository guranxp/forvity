package com.forvity.app.system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "app.bootstrap.admin.email=root@example.com",
        "app.bootstrap.admin.password=secret123"
})
class BootstrapServiceIT {

    @Autowired
    private SystemRoleRepository systemRoleRepository;

    @Autowired
    private SystemAccountRepository systemAccountRepository;

    @Test
    void shouldCreateRootAccountOnStartup() {
        assertThat(systemRoleRepository.existsByRole(SystemRoleType.ROOT)).isTrue();
        assertThat(systemAccountRepository.findByEmail("root@example.com")).isPresent();
    }

    @Test
    void shouldNotCreateDuplicateRootOnSecondStartup() {
        assertThat(systemRoleRepository.findAll().stream()
            .filter(r -> r.getRole() == SystemRoleType.ROOT)
            .count()).isEqualTo(1);
    }
}