package com.forvity.app.member;

import com.forvity.app.shared.AuditableEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "system_roles")
public class SystemRole extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SystemRoleType role;

    public UUID getId() { return id; }
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
    public SystemRoleType getRole() { return role; }
    public void setRole(SystemRoleType role) { this.role = role; }
}