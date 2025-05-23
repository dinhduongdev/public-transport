package com.publictransport.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class ScheduleDTO {
    private Long id;
    private Long routeVariantId;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;
    private Integer priority;
    private List<String> startTimes;
    private List<String> endTimes;
    private List<String> licenses;
}
