package com.forvity.app.system;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SystemAccountRepository extends JpaRepository<SystemAccount, UUID> {

    boolean existsByEmail(String email);

    Optional<SystemAccount> findByEmail(String email);
}