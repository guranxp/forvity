package com.forvity.app.system;

import com.forvity.app.shared.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static org.springframework.util.Assert.hasText;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "system_accounts")
public class SystemAccount extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    public SystemAccount(final String email, final String password) {
        hasText(email, "Email must not be blank");
        hasText(password, "Password must not be blank");
        this.email = email;
        this.password = password;
    }
}