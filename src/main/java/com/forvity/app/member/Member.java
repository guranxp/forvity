package com.forvity.app.member;

import com.forvity.app.shared.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "members", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email", "club_id"}),
        @UniqueConstraint(columnNames = {"username", "club_id"})
})
public class Member extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "club_id", nullable = false)
    private UUID clubId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "member_roles", joinColumns = @JoinColumn(name = "member_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<MemberRoleType> roles;

    public Member(
            final UUID clubId,
            final String email,
            final String username,
            final String password,
            final Set<MemberRoleType> roles) {
        notNull(clubId, "Club ID must not be null");
        hasText(email, "Email must not be blank");
        hasText(username, "Username must not be blank");
        hasText(password, "Password must not be blank");
        notNull(roles, "Roles must not be null");
        this.clubId = clubId;
        this.email = email;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }
}