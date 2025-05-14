package com.publictransport.repositories.impl;

import com.publictransport.models.Stop;
import com.publictransport.repositories.StopRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class StopRepositoryImpl implements StopRepository {

    private final SessionFactory factory;

    @Autowired
    public StopRepositoryImpl(SessionFactory factory) {
        this.factory = factory;
    }

    private Session getCurrentSession() {
        return factory.getCurrentSession();
    }

    @Override
    public List<Stop> findStopsByRouteVariantId(Long routeVariantId) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Stop> cq = cb.createQuery(Stop.class);
        Root<Stop> root = cq.from(Stop.class);

        cq.select(root)
                .where(cb.equal(root.get("routeVariant").get("id"), routeVariantId))
                .orderBy(cb.asc(root.get("stopOrder")));

        return session.createQuery(cq).getResultList();
    }

    @Override
    public void save(Stop stop) {
        Session session = getCurrentSession();
        session.persist(stop);
    }

    @Override
    public Stop findById(Long id) {
        Session session = getCurrentSession();
        return session.get(Stop.class, id);
    }

    @Override
    public void deleteByRouteVariantId(Long routeVariantId) {
        Session session = getCurrentSession();
        List<Stop> stops = findStopsByRouteVariantId(routeVariantId);
        for (Stop stop : stops) {
            Stop stopToDelete = findById(stop.getId());
            if (stopToDelete != null) {
                session.remove(stopToDelete);
            }
        }
    }
}