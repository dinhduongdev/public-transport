package com.publictransport.services.impl;

import com.publictransport.repositories.ScheduleRepository;
import com.publictransport.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    ScheduleRepository scheduleRepository;
    @Override
    public List<Schedule> getSchedules() {
        return this.scheduleRepository.getSchedules();
    }

    @Override
    public Schedule getScheduleById(Long id) {
        return this.scheduleRepository.getScheduleById(id);
    }

    @Override
    public Schedule saveSchedule(Schedule schedule) {
        return this.scheduleRepository.saveSchedule(schedule);
    }

    @Override
    public void deleteSchedule(Long id) {
        this.scheduleRepository.deleteSchedule(id);
    }

    @Override
    public List<Schedule> getByStartTime(Date startTime) {
        return this.scheduleRepository.getByStartTime(startTime);
    }

    @Override
    public List<Schedule> getByEndTime(Date endTime) {
        return this.scheduleRepository.getByEndTime(endTime);
    }

    @Override
    public List<Schedule> getByRouteId(Long routeId) {
        return this.scheduleRepository.getByRouteId(routeId);
    }

    @Override
    public List<Schedule> getByVehicleId(Long vehicleId) {
        return this.scheduleRepository.getByVehicleId(vehicleId);
    }
}
