package com.forvity.app.system;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface SystemRoleRepository extends JpaRepository<SystemRole, UUID> {

    boolean existsByRole(SystemRoleType role);
}