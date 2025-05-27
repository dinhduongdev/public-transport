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
import java.util.Optional;

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
    public Optional<Stop> findById(Long id) {
        Session session = getCurrentSession();
        return Optional.ofNullable(session.get(Stop.class, id));
    }

    @Override
    public void deleteByRouteVariantId(Long routeVariantId) {
        Session session = getCurrentSession();
        String hql = "delete from Stop s where s.routeVariant.id = :routeVariantId";
        session.createQuery(hql, Stop.class)
                .setParameter("routeVariantId", routeVariantId)
                .executeUpdate();
    }
}

