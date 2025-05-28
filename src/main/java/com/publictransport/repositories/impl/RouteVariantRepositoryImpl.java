package com.publictransport.repositories.impl;

import com.publictransport.models.*;
import com.publictransport.repositories.RouteVariantRepository;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Transactional
public class RouteVariantRepositoryImpl implements RouteVariantRepository {
    private final SessionFactory factory;

    @Autowired
    public RouteVariantRepositoryImpl(SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public List<RouteVariant> findAllRouteVariants(int page, int size) {
        return getRouteVariants(null, page, size);
    }

    @Override
    public long countAllRouteVariants() {
        Session session = factory.getCurrentSession();
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
        Session session = factory.getCurrentSession();
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
    public Optional<RouteVariant> findById(Long id) {
        Session session = factory.getCurrentSession();
        return Optional.ofNullable(session.get(RouteVariant.class, id));
    }

    @Override
    public void save(RouteVariant routeVariant) {
        Session session = factory.getCurrentSession();
        session.persist(routeVariant);
    }

    @Override
    public void update(RouteVariant routeVariant) {
        Session session = factory.getCurrentSession();
        session.merge(routeVariant);
    }

    @Override
    public void delete(Long id) {
        Session session = factory.getCurrentSession();
        RouteVariant routeVariant = findById(id).orElseThrow();
        session.remove(routeVariant);
    }

    @Override
    public List<RouteVariant> findByRouteId(Long routeId) {
        Session session = factory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<RouteVariant> cq = cb.createQuery(RouteVariant.class);
        Root<RouteVariant> root = cq.from(RouteVariant.class);

        // Thêm điều kiện lọc theo routeId
        Predicate routeIdPredicate = cb.equal(root.get("route").get("id"), routeId);
        cq.select(root).where(routeIdPredicate);

        return session.createQuery(cq).getResultList();
    }

    private List<RouteVariant> getRouteVariants(Map<String, String> params, int page, int size) {
        Session session = factory.getCurrentSession();
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

    @Override
    public List<RouteVariant> findByStationId(Long stationId) {
        Session session = factory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<RouteVariant> cq = cb.createQuery(RouteVariant.class);
        Root<RouteVariant> root = cq.from(RouteVariant.class);
        Join<RouteVariant, Stop> stopJoin = root.join(RouteVariant_.stops, JoinType.INNER);

        Predicate stationIdPredicate = cb.equal(stopJoin.get(Stop_.station).get(Station_.id), stationId);
        cq.select(root).where(stationIdPredicate);

        return session.createQuery(cq).getResultList();
    }

    @Override
    public Optional<Double> findMeanDistanceBetweenStops(Long routeVariantId) {
        Session session = factory.getCurrentSession();
        String hql = "SELECT rv.distance / (COUNT(s.id) - 1) " +
                "FROM RouteVariant rv JOIN rv.stops s " +
                "WHERE rv.id = :routeVariantId " +
                "GROUP BY rv.distance " +
                "HAVING COUNT(s.id) > 1";
        Double meanDistance = (Double) session.createQuery(hql)
                .setParameter("routeVariantId", routeVariantId)
                .uniqueResult();
        if (meanDistance == null || meanDistance.isNaN() || meanDistance.isInfinite()) {
            return Optional.empty();
        }
        return Optional.of(meanDistance);
    }
}
