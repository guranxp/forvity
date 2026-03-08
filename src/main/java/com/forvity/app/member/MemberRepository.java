package com.forvity.app.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    boolean existsByEmailAndClubId(String email, UUID clubId);

    boolean existsByUsernameAndClubId(String username, UUID clubId);

    Optional<Member> findByEmailAndClubId(String email, UUID clubId);
}