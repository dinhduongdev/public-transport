package com.publictransport.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_variant_id")
    private RouteVariant routeVariant;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "priority")
    private Integer priority;

    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(
            name = "Schedule_Day",
            joinColumns = @JoinColumn(name = "schedule_id")
    )
    @Column(name = "day", columnDefinition = "ENUM('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')")
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> daysOfWeek;
}