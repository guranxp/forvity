package com.forvity.app.system;

import com.forvity.app.shared.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static org.springframework.util.Assert.notNull;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "system_roles")
public class SystemRole extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_account_id", nullable = false)
    private SystemAccount systemAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SystemRoleType role;

    public SystemRole(final SystemAccount systemAccount, final SystemRoleType role) {
        notNull(systemAccount, "systemAccount must not be null");
        notNull(role, "role must not be null");
        this.systemAccount = systemAccount;
        this.role = role;
    }
}