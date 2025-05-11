package com.publictransport.services;

import com.publictransport.models.ScheduleTrip;

import java.util.List;

public interface ScheduleTripService {
    List<ScheduleTrip> findByScheduleId(Long scheduleId, int page, int size);
    long countByScheduleId(Long scheduleId);
}
