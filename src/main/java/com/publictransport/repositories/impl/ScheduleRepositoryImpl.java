package com.publictransport.repositories.impl;

import com.publictransport.repositories.ScheduleRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class ScheduleRepositoryImpl implements ScheduleRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    private Session getCurrentSession() {
        return factory.getObject().getCurrentSession();
    }

    @Override
    public List<Schedule> getSchedules() {
        Session session = getCurrentSession();
        Query<Schedule> query = session.createQuery("FROM Schedule", Schedule.class);
        return query.getResultList();
    }

    @Override
    public Schedule getScheduleById(Long id) {
        Session session = getCurrentSession();
        return session.get(Schedule.class, id);
    }

    @Override
    public Schedule saveSchedule(Schedule schedule) {
        Session session = getCurrentSession();
        session.saveOrUpdate(schedule);
        return schedule;
    }

    @Override
    public void deleteSchedule(Long id) {
        Session session = getCurrentSession();
        Schedule schedule = session.get(Schedule.class, id);
        if (schedule != null) {
            session.delete(schedule);
        }
    }

    @Override
    public List<Schedule> getByStartTime(Date startTime) {
        Session session = getCurrentSession();
        Query<Schedule> query = session.createQuery("FROM Schedule s WHERE s.startTime = :startTime", Schedule.class);
        query.setParameter("startTime", startTime);
        return query.getResultList();
    }

    @Override
    public List<Schedule> getByEndTime(Date endTime) {
        Session session = getCurrentSession();
        Query<Schedule> query = session.createQuery("FROM Schedule s WHERE s.endTime = :endTime", Schedule.class);
        query.setParameter("endTime", endTime);
        return query.getResultList();
    }

    @Override
    public List<Schedule> getByRouteId(Long routeId) {
        Session session = getCurrentSession();
        Query<Schedule> query = session.createQuery("FROM Schedule s WHERE s.routeId.id = :routeId", Schedule.class);
        query.setParameter("routeId", routeId);
        return query.getResultList();
    }

    @Override
    public List<Schedule> getByVehicleId(Long vehicleId) {
        Session session = getCurrentSession();
        Query<Schedule> query = session.createQuery("FROM Schedule s WHERE s.vehicleId.id = :vehicleId", Schedule.class);
        query.setParameter("vehicleId", vehicleId);
        return query.getResultList();
    }
}