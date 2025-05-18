package com.publictransport.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "schedule_day")
public class ScheduleDay {
    @EmbeddedId
    private ScheduleDayId id;

    @MapsId("scheduleId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

}