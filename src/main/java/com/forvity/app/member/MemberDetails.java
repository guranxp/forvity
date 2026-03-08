package com.forvity.app.member;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toSet;
import static org.springframework.util.Assert.notNull;

public class MemberDetails implements UserDetails {

    private final UUID clubId;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    private MemberDetails(
            final UUID clubId,
            final String email,
            final String password,
            final Collection<? extends GrantedAuthority> authorities) {
        this.clubId = clubId;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static MemberDetails from(final Member member) {
        notNull(member, "member must not be null");
        final Set<GrantedAuthority> authorities = member.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
            .collect(toSet());
        return new MemberDetails(member.getClub().getId(), member.getEmail(), member.getPassword(), authorities);
    }

    public UUID getClubId() {
        return clubId;
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