package com.publictransport.services.impl;

import com.publictransport.models.ScheduleDay;
import com.publictransport.repositories.ScheduleDayRepository;
import com.publictransport.services.ScheduleDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class ScheduleDayServiceImpl implements ScheduleDayService {
    @Autowired
    private ScheduleDayRepository scheduleDayRepository;
    @Override
    public void save(ScheduleDay scheduleDay) {
        this.scheduleDayRepository.save(scheduleDay);
    }
}
