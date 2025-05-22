package com.publictransport.services.impl;

import com.publictransport.dto.ScheduleDTO;
import com.publictransport.models.RouteVariant;
import com.publictransport.models.Schedule;
import com.publictransport.models.ScheduleTrip;
import com.publictransport.repositories.ScheduleRepository;
import com.publictransport.services.RouteVariantService;
import com.publictransport.services.ScheduleService;
import com.publictransport.services.ScheduleTripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private RouteVariantService routeVariantService;
    @Autowired
    private ScheduleTripService scheduleTripService;

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

    @Override
    public void saveFromDTO(ScheduleDTO scheduleDTO) {
        RouteVariant routeVariant = routeVariantService.findById(scheduleDTO.getRouteVariantId());
        if (routeVariant == null) {
            throw new IllegalArgumentException("Tuyến đường không tồn tại.");
        }
        // chuyển qua Schedule
        Schedule schedule = new Schedule();
        schedule.setRouteVariant(routeVariant);
        schedule.setStartDate(scheduleDTO.getStartDate());
        schedule.setEndDate(scheduleDTO.getEndDate());
        schedule.setPriority(scheduleDTO.getPriority());
        scheduleRepository.save(schedule);
        //ScheduleTrips
        List<String> startTimes = scheduleDTO.getStartTimes();
        List<String> endTimes = scheduleDTO.getEndTimes();
        if (startTimes != null && endTimes != null && startTimes.size() == endTimes.size()) {
            for (int i = 0; i < startTimes.size(); i++) {
                String startTimeStr = startTimes.get(i);
                String endTimeStr = endTimes.get(i);
                // Bỏ qua nếu cả startTime và endTime đều rỗng
                if (startTimeStr.isEmpty() && endTimeStr.isEmpty()) {
                    continue;
                }
                if (startTimeStr.isEmpty() || endTimeStr.isEmpty()) {
                    throw new IllegalArgumentException("Thời gian bắt đầu và kết thúc của chuyến không được để trống.");
                }
                ScheduleTrip trip = new ScheduleTrip();
                trip.setSchedule(schedule);
                trip.setStartTime(LocalTime.parse(startTimeStr));
                trip.setEndTime(LocalTime.parse(endTimeStr));
                scheduleTripService.save(trip);
            }
        }

    }
}
