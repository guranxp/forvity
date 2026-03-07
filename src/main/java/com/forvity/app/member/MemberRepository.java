package com.forvity.app.member;

import com.forvity.app.club.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    boolean existsByEmailAndClub(String email, Club club);

    boolean existsByUsernameAndClub(String username, Club club);
}