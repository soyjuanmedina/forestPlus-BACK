package com.forestplus.security;

import com.forestplus.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(UserEntity user, Collection<? extends GrantedAuthority> authorities) {
        this.id = user.getId();
        this.username = user.getEmail(); // o el campo que uses como login
        this.password = user.getPasswordHash();
        this.authorities = authorities;
    }

    // 🔹 Este método es obligatorio de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
