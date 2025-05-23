package com.publictransport.services;

import com.publictransport.dto.ScheduleDTO;
import com.publictransport.models.Schedule;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    List<Schedule> findAllSchedules(int page, int size);
    long countAllSchedules();
    List<Schedule> searchSchedules(Map<String, String> params, int page, int size);
    long countSchedulesByParams(Map<String, String> params);
    Schedule findById(Long id);
    void save(Schedule schedule);
    void update(Schedule schedule);
    void delete(Long id);
    void saveFromDTO(ScheduleDTO scheduleDTO);
    void updateFromDTO(ScheduleDTO scheduleDTO);
}
