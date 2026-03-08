package com.forvity.app.club;

import com.forvity.app.shared.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static org.springframework.util.Assert.hasText;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "clubs")
public class Club extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    public Club(final String name, final String slug) {
        hasText(name, "Name must not be blank");
        hasText(slug, "Slug must not be blank");
        this.name = name;
        this.slug = slug;
    }
}