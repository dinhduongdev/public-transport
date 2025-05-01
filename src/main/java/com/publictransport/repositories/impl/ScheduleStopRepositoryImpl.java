package com.publictransport.repositories.impl;

import com.publictransport.models.ScheduleStop;
import com.publictransport.repositories.ScheduleStopRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public class ScheduleStopRepositoryImpl implements ScheduleStopRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    // Helper method to get current Hibernate session
    private Session getCurrentSession() {
        return factory.getObject().getCurrentSession();
    }

    @Override
    public List<ScheduleStop> getScheduleStops() {
        Session session = getCurrentSession();
        Query<ScheduleStop> query = session.createQuery("FROM ScheduleStop", ScheduleStop.class);
        return query.getResultList();
    }

    @Override
    public ScheduleStop getScheduleStopById(Long id) {
        Session session = getCurrentSession();
        return session.get(ScheduleStop.class, id);
    }

    @Override
    public ScheduleStop saveScheduleStop(ScheduleStop scheduleStop) {
        Session session = getCurrentSession();
        session.saveOrUpdate(scheduleStop);
        return scheduleStop;
    }

    @Override
    public void deleteScheduleStop(Long id) {
        Session session = getCurrentSession();
        ScheduleStop scheduleStop = session.get(ScheduleStop.class, id);
        if (scheduleStop != null) {
            session.delete(scheduleStop);
        }
    }

    @Override
    public List<ScheduleStop> getByScheduleId(Long scheduleId) {
        Session session = getCurrentSession();
        Query<ScheduleStop> query = session.createQuery("FROM ScheduleStop ss WHERE ss.scheduleId.id = :scheduleId", ScheduleStop.class);
        query.setParameter("scheduleId", scheduleId);
        return query.getResultList();
    }

    @Override
    public void deleteByScheduleId(Long scheduleId) {
        Session session = getCurrentSession();
        Query<?> query = session.createQuery("DELETE FROM ScheduleStop ss WHERE ss.scheduleId.id = :scheduleId");
        query.setParameter("scheduleId", scheduleId);
        query.executeUpdate();
    }

    @Override
    public List<ScheduleStop> getByArrivalTime(Date arrivalTime) {
        Session session = getCurrentSession();
        Query<ScheduleStop> query = session.createQuery("FROM ScheduleStop ss WHERE ss.arrivalTime = :arrivalTime", ScheduleStop.class);
        query.setParameter("arrivalTime", arrivalTime);
        return query.getResultList();
    }
}