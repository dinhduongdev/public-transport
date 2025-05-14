package com.publictransport.repositories.impl;

import com.publictransport.models.RouteVariant;
import com.publictransport.repositories.RouteVariantRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class RouteVariantRepositoryImpl implements RouteVariantRepository {
    private final SessionFactory factory;

    @Autowired
    public RouteVariantRepositoryImpl(SessionFactory factory) {
        this.factory = factory;
    }

    private Session getCurrentSession() {
        return factory.getCurrentSession();
    }


    @Override
    public List<RouteVariant> findAllRouteVariants(int page, int size) {
        return getRouteVariants(null, page, size);
    }

    @Override
    public long countAllRouteVariants() {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<RouteVariant> root = cq.from(RouteVariant.class);
        cq.select(cb.count(root));

        return session.createQuery(cq).getSingleResult();
    }

    @Override
    public List<RouteVariant> searchRouteVariants(Map<String, String> params, int page, int size) {
        return getRouteVariants(params, page, size);
    }

    @Override
    public long countRouteVariantsByParams(Map<String, String> params) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<RouteVariant> root = cq.from(RouteVariant.class);

        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            if (params.containsKey("name")) {
                String namePattern = "%" + params.get("name").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), namePattern));
            }
            if (params.containsKey("startStop")) {
                String startStopPattern = "%" + params.get("startStop").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("startStop")), startStopPattern));
            }
            if (params.containsKey("endStop")) {
                String endStopPattern = "%" + params.get("endStop").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("endStop")), endStopPattern));
            }
        }

        cq.select(cb.count(root)).where(predicates.toArray(new Predicate[0]));

        return session.createQuery(cq).getSingleResult();
    }

    @Override
    public RouteVariant findById(Long id) {
        Session session = getCurrentSession();
        return session.get(RouteVariant.class, id);
    }

    @Override
    public void save(RouteVariant routeVariant) {
        Session session = getCurrentSession();
        session.persist(routeVariant);
    }

    @Override
    public void update(RouteVariant routeVariant) {
        Session session = getCurrentSession();
        session.merge(routeVariant);
    }

    @Override
    public void delete(Long id) {
        Session session = getCurrentSession();
        RouteVariant routeVariant = findById(id);
        if (routeVariant != null) {
            session.remove(routeVariant);
        }
    }

    @Override
    public List<RouteVariant> findByRouteId(Long routeId) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<RouteVariant> cq = cb.createQuery(RouteVariant.class);
        Root<RouteVariant> root = cq.from(RouteVariant.class);

        // Thêm điều kiện lọc theo routeId
        Predicate routeIdPredicate = cb.equal(root.get("route").get("id"), routeId);
        cq.select(root).where(routeIdPredicate);

        return session.createQuery(cq).getResultList();
    }

    private List<RouteVariant> getRouteVariants(Map<String, String> params, int page, int size) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<RouteVariant> cq = cb.createQuery(RouteVariant.class);
        Root<RouteVariant> root = cq.from(RouteVariant.class);
        cq.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            if (params.containsKey("name")) {
                String namePattern = "%" + params.get("name").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), namePattern));
            }
            if (params.containsKey("startStop")) {
                String startStopPattern = "%" + params.get("startStop").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("startStop")), startStopPattern));
            }
            if (params.containsKey("endStop")) {
                String endStopPattern = "%" + params.get("endStop").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("endStop")), endStopPattern));
            }
            cq.where(predicates.toArray(new Predicate[0]));
        }

        Query<RouteVariant> query = session.createQuery(cq);

        if (page > 0 && size > 0) {
            int start = (page - 1) * size;
            query.setMaxResults(size);
            query.setFirstResult(start);
        }

        return query.getResultList();
    }
}
