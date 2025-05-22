package com.publictransport.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class ScheduleDTO {
    private Long routeVariantId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer priority;
    private List<String> startTimes;
    private List<String> endTimes;
}
