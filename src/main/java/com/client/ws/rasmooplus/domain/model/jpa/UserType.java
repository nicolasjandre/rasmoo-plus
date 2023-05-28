package com.client.ws.rasmooplus.domain.model.jpa;

import org.springframework.security.core.GrantedAuthority;

import com.client.ws.rasmooplus.config.JacocoConfig.Generated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_type")
public class UserType implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_type_id")
    private Long id;

    private String name;

    private String description;

    @Override
    @Generated
    public String getAuthority() {
        return name;
    }
}
