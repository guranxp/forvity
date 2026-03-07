package com.forvity.app.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}