package com.publictransport.repositories.impl;

import com.publictransport.models.ScheduleTrip;
import com.publictransport.repositories.ScheduleTripRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@Transactional
public class ScheduleTripRepositoryImpl implements ScheduleTripRepository {
    @Autowired
    private LocalSessionFactoryBean factory;

    private Session getCurrentSession() {
        return factory.getObject().getCurrentSession();
    }

    @Override
    public List<ScheduleTrip> findByScheduleId(Long scheduleId, int page, int size) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ScheduleTrip> cq = cb.createQuery(ScheduleTrip.class);
        Root<ScheduleTrip> root = cq.from(ScheduleTrip.class);

        cq.select(root).where(cb.equal(root.get("schedule").get("id"), scheduleId));

        Query<ScheduleTrip> query = session.createQuery(cq);

        if (page > 0 && size > 0) {
            int start = (page - 1) * size;
            query.setMaxResults(size);
            query.setFirstResult(start);
        }

        return query.getResultList();
    }

    @Override
    public long countByScheduleId(Long scheduleId) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ScheduleTrip> root = cq.from(ScheduleTrip.class);

        cq.select(cb.count(root)).where(cb.equal(root.get("schedule").get("id"), scheduleId));

        return session.createQuery(cq).getSingleResult();
    }
}
