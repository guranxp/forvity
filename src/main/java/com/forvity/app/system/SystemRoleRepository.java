package com.forvity.app.system;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface SystemRoleRepository extends JpaRepository<SystemRole, UUID> {

    boolean existsByRole(SystemRoleType role);

    List<SystemRole> findAllBySystemAccountId(UUID systemAccountId);
}