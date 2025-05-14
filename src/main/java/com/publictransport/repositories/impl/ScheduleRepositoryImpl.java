package com.publictransport.repositories.impl;

import com.publictransport.models.Schedule;
import com.publictransport.repositories.ScheduleRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Repository
@Transactional
public class ScheduleRepositoryImpl implements ScheduleRepository {
    private static final int PAGE_SIZE = 10;
    private final SessionFactory factory;

    @Autowired
    public ScheduleRepositoryImpl(SessionFactory factory) {
        this.factory = factory;
    }

    private Session getCurrentSession() {
        return factory.getCurrentSession();
    }

    @Override
    public List<Schedule> findAllSchedules(int page, int size) {
        return getSchedules(null, page, size);
    }

    @Override
    public long countAllSchedules() {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Schedule> root = cq.from(Schedule.class);

        cq.select(cb.count(root));

        return session.createQuery(cq).getSingleResult();
    }

    @Override
    public List<Schedule> searchSchedules(Map<String, String> params, int page, int size) {
        return getSchedules(params, page, size);
    }

    @Override
    public long countSchedulesByParams(Map<String, String> params) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Schedule> root = cq.from(Schedule.class);

        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            if (params.containsKey("routeVariantId")) {
                Long routeVariantId = Long.valueOf(params.get("routeVariantId"));
                predicates.add(cb.equal(root.get("routeVariant").get("id"), routeVariantId));
            }
            if (params.containsKey("startDate")) {
                Instant startDate = Instant.parse(params.get("startDate"));
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), startDate));
            }
            if (params.containsKey("endDate")) {
                Instant endDate = Instant.parse(params.get("endDate"));
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), endDate));
            }
            if (params.containsKey("priority")) {
                Integer priority = Integer.valueOf(params.get("priority"));
                predicates.add(cb.equal(root.get("priority"), priority));
            }
        }

        cq.select(cb.count(root)).where(predicates.toArray(new Predicate[0]));

        return session.createQuery(cq).getSingleResult();
    }

    @Override
    public Schedule findById(Long id) {
        Session session = getCurrentSession();
        return session.get(Schedule.class, id);
    }

    @Override
    public void save(Schedule schedule) {
        Session session = getCurrentSession();
        session.persist(schedule);
    }

    @Override
    public void update(Schedule schedule) {
        Session session = getCurrentSession();
        session.merge(schedule);
    }

    @Override
    public void delete(Long id) {
        Session session = getCurrentSession();
        Schedule schedule = findById(id);
        if (schedule != null) {
            session.remove(schedule);
        }
    }

    private List<Schedule> getSchedules(Map<String, String> params, int page, int size) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Schedule> cq = cb.createQuery(Schedule.class);
        Root<Schedule> root = cq.from(Schedule.class);
        cq.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            if (params.containsKey("routeVariantId")) {
                Long routeVariantId = Long.valueOf(params.get("routeVariantId"));
                predicates.add(cb.equal(root.get("routeVariant").get("id"), routeVariantId));
            }
            if (params.containsKey("startDate")) {
                Instant startDate = Instant.parse(params.get("startDate"));
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), startDate));
            }
            if (params.containsKey("endDate")) {
                Instant endDate = Instant.parse(params.get("endDate"));
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), endDate));
            }
            if (params.containsKey("priority")) {
                Integer priority = Integer.valueOf(params.get("priority"));
                predicates.add(cb.equal(root.get("priority"), priority));
            }
            cq.where(predicates.toArray(new Predicate[0]));
        }

        Query<Schedule> query = session.createQuery(cq);

        if (page > 0 && size > 0) {
            int start = (page - 1) * size;
            query.setMaxResults(size);
            query.setFirstResult(start);
        }

        return query.getResultList();
    }
}
