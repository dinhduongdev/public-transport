package com.publictransport.repositories.impl;

import com.publictransport.models.ScheduleDay;
import com.publictransport.repositories.ScheduleDayRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ScheduleDayRepositoryImpl implements ScheduleDayRepository {
    private final SessionFactory factory;

    @Autowired
    public ScheduleDayRepositoryImpl(SessionFactory factory) {
        this.factory = factory;
    }
    private Session getCurrentSession() {
        return factory.getCurrentSession();
    }
    @Override
    public void save(ScheduleDay scheduleDay) {
        getCurrentSession().persist(scheduleDay);
    }
}
