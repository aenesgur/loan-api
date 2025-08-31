package com.aenesgur.banking.loan.domain.entity;

import com.aenesgur.banking.loan.domain.entity.base.BaseEntity;
import com.aenesgur.banking.loan.domain.enumz.RoleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType name;
}
