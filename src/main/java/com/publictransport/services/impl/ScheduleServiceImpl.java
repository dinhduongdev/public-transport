package com.publictransport.services.impl;

import com.publictransport.models.Schedule;
import com.publictransport.repositories.ScheduleRepository;
import com.publictransport.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public List<Schedule> findAllSchedules(int page, int size) {
        return scheduleRepository.findAllSchedules(page, size);
    }

    @Override
    public long countAllSchedules() {
        return scheduleRepository.countAllSchedules();
    }

    @Override
    public List<Schedule> searchSchedules(Map<String, String> params, int page, int size) {
        return scheduleRepository.searchSchedules(params, page, size);
    }

    @Override
    public long countSchedulesByParams(Map<String, String> params) {
        return scheduleRepository.countSchedulesByParams(params);
    }

    @Override
    public Schedule findById(Long id) {
        return scheduleRepository.findById(id);
    }

    @Override
    public void save(Schedule schedule) {
        scheduleRepository.save(schedule);
    }

    @Override
    public void update(Schedule schedule) {
        scheduleRepository.save(schedule);
    }

    @Override
    public void delete(Long id) {
        scheduleRepository.delete(id);
    }
}
