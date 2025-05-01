package com.publictransport.services;

import com.publictransport.models.Schedule;

import java.util.Date;
import java.util.List;

public interface ScheduleService {
    List<Schedule> getSchedules();
    Schedule getScheduleById(Long id);
    Schedule saveSchedule(Schedule schedule);
    void deleteSchedule(Long id);
    List<Schedule> getByStartTime(Date startTime);
    List<Schedule> getByEndTime(Date endTime);
    List<Schedule> getByRouteId(Long routeId);
    List<Schedule> getByVehicleId(Long vehicleId);
}
