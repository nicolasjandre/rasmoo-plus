package com.client.ws.rasmooplus.domain.model.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.client.ws.rasmooplus.config.JacocoConfig.Generated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_credentials")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentials implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_credentials_id")
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_type_id")
    private UserType userType;

    @Override
    @Generated
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<UserType> userTypes = new ArrayList<>();
        userTypes.add(userType);
        return userTypes;
    }

    @Override
    @Generated
    public String getUsername() {
        return username;
    }

    @Override
    @Generated
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Generated
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Generated
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Generated
    public boolean isEnabled() {
        return true;
    }
}
