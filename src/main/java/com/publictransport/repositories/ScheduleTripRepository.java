package com.publictransport.repositories;


import com.publictransport.models.ScheduleTrip;

import java.util.List;

public interface ScheduleTripRepository {
    List<ScheduleTrip> findByScheduleId(Long scheduleId, int page, int size);
    long countByScheduleId(Long scheduleId);
    void save(ScheduleTrip scheduleTrip);
}