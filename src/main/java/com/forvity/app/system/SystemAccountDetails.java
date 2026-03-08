package com.forvity.app.system;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.springframework.util.Assert.notNull;

public record SystemAccountDetails(
        String email,
        String password,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    public static SystemAccountDetails from(final SystemAccount account, final List<SystemRole> roles) {
        notNull(account, "account must not be null");
        notNull(roles, "roles must not be null");
        final var authorities = roles.stream()
                .flatMap(role -> authoritiesFor(role.getRole()).stream())
                .collect(toSet());
        return new SystemAccountDetails(account.getEmail(), account.getPassword(), authorities);
    }

    private static Set<GrantedAuthority> authoritiesFor(final SystemRoleType roleType) {
        return switch (roleType) {
            case ROOT -> Set.of(new SimpleGrantedAuthority("ROLE_ROOT"), new SimpleGrantedAuthority("ROLE_SUPERADMIN"));
            case SUPERADMIN -> Set.of(new SimpleGrantedAuthority("ROLE_SUPERADMIN"));
        };
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}