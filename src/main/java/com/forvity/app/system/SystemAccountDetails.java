package com.forvity.app.system;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SystemAccountDetails implements UserDetails {

    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    private SystemAccountDetails(final String email, final String password, final Collection<? extends GrantedAuthority> authorities) {
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static SystemAccountDetails from(final SystemAccount account, final List<SystemRole> roles) {
        final var authorities = roles.stream()
                .flatMap(role -> authoritiesFor(role.getRole()).stream())
                .collect(Collectors.toSet());
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