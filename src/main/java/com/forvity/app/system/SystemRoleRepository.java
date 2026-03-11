package com.forvity.app.system;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SystemRoleRepository extends JpaRepository<SystemRole, UUID> {

    boolean existsByRole(SystemRoleType role);

    List<SystemRole> findAllBySystemAccountId(UUID systemAccountId);

    long countByRoleAndDeletedAtIsNull(SystemRoleType role);

    Optional<SystemRole> findByIdAndDeletedAtIsNull(UUID id);
}