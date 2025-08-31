package com.aenesgur.banking.loan.security;

import com.aenesgur.banking.loan.domain.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final UUID id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Optional<UUID> customerId;

    public CustomUserDetails(UUID id, String username, String password, Collection<? extends GrantedAuthority> authorities, UUID customerId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.customerId = Optional.ofNullable(customerId);
    }

    // Kullanıcı ID'sini döndürür
    public UUID getUserId() {
        return id;
    }

    // Customer ID'sini döndürür. Optional kullanarak null değerleri güvenli bir şekilde yönetir.
    public Optional<UUID> getCustomerId() {
        return customerId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Hesap geçerlilik süresi dolmamış
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Hesap kilitli değil
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Kimlik bilgileri süresi dolmamış
    }

    @Override
    public boolean isEnabled() {
        return true; // Hesap etkin
    }

    // CustomUserDetails'i UserDetails'e dönüştüren yardımcı metot
    public static CustomUserDetails create(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().toString()))
                .collect(Collectors.toList());

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities,
                user.getCustomerId()
        );
    }
}