package com.publictransport.services.impl;

import com.publictransport.models.ScheduleTrip;
import com.publictransport.repositories.ScheduleTripRepository;
import com.publictransport.services.ScheduleTripService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class ScheduleTripServiceImpl implements ScheduleTripService {
    @Autowired
    private ScheduleTripRepository scheduleTripRepository;

    @Override
    public List<ScheduleTrip> findByScheduleId(Long scheduleId, int page, int size) {
        return scheduleTripRepository.findByScheduleId(scheduleId, page, size);
    }

    @Override
    public long countByScheduleId(Long scheduleId) {
        return scheduleTripRepository.countByScheduleId(scheduleId);
    }
}
