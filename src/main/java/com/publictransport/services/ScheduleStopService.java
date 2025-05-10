package com.publictransport.services;

import java.util.Date;
import java.util.List;

public interface ScheduleStopService {
    List<ScheduleStop> getScheduleStops();
    ScheduleStop getScheduleStopById(Long id);
    ScheduleStop saveScheduleStop(ScheduleStop scheduleStop);
    void deleteScheduleStop(Long id);
    List<ScheduleStop> getByScheduleId(Long scheduleId);
    void deleteByScheduleId(Long scheduleId);
    List<ScheduleStop> getByArrivalTime(Date arrivalTime);
}
