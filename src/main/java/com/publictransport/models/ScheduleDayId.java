package com.publictransport.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ScheduleDayId implements Serializable {
    private static final long serialVersionUID = -7749700939185503240L;
    @NotNull
    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Size(max = 10)
    @NotNull
    @Column(name = "day", nullable = false, length = 10)
    private String day;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ScheduleDayId entity = (ScheduleDayId) o;
        return Objects.equals(this.day, entity.day) &&
                Objects.equals(this.scheduleId, entity.scheduleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, scheduleId);
    }

}