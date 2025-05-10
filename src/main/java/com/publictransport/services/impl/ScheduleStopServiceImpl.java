package com.publictransport.services.impl;

import com.publictransport.repositories.ScheduleStopRepository;
import com.publictransport.services.ScheduleStopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ScheduleStopServiceImpl implements ScheduleStopService {
    @Autowired
    ScheduleStopRepository scheduleStopRepository;
    @Override
    public List<ScheduleStop> getScheduleStops() {
        return this.scheduleStopRepository.getScheduleStops();
    }

    @Override
    public ScheduleStop getScheduleStopById(Long id) {
        return this.scheduleStopRepository.getScheduleStopById(id);
    }

    @Override
    public ScheduleStop saveScheduleStop(ScheduleStop scheduleStop) {
        return this.scheduleStopRepository.saveScheduleStop(scheduleStop);
    }

    @Override
    public void deleteScheduleStop(Long id) {
        this.scheduleStopRepository.deleteScheduleStop(id);
    }

    @Override
    public List<ScheduleStop> getByScheduleId(Long scheduleId) {
        return this.scheduleStopRepository.getByScheduleId(scheduleId);
    }

    @Override
    public void deleteByScheduleId(Long scheduleId) {
        this.scheduleStopRepository.deleteByScheduleId(scheduleId);
    }

    @Override
    public List<ScheduleStop> getByArrivalTime(Date arrivalTime) {
        return this.scheduleStopRepository.getByArrivalTime(arrivalTime);
    }
}
