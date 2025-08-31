package com.aenesgur.banking.loan.domain.entity.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity extends BaseAuditingEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Version
    @Column(name = "version")
    private int version;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final BaseEntity that = (BaseEntity) o;
        return version == that.version && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version);
    }
}
