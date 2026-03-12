package com.forvity.app.club;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ClubRepository extends JpaRepository<Club, UUID> {

    boolean existsBySlug(String slug);

    Optional<Club> findBySlug(String slug);

    List<Club> findAllByDeletedAtIsNull();
}