package com.publictransport.repositories.impl;

import com.publictransport.models.Route;
import com.publictransport.models.RouteVariant;
import com.publictransport.repositories.RouteRepository;
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

@Repository
@Transactional
public class RouteRepositoryImpl implements RouteRepository {

    private static final int PAGE_SIZE = 10;
    private final SessionFactory factory;

    @Autowired
    public RouteRepositoryImpl(SessionFactory factory) {
        this.factory = factory;
    }

    private Session getCurrentSession() {
        return factory.getCurrentSession();
    }

    @Override
    public List<Route> findAllRoutes(int page, int size) {
        return getRoutes(null, page, size);
    }

    @Override
    public long countAllRoutes() {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Route> root = cq.from(Route.class);

        cq.select(cb.count(root));

        return session.createQuery(cq).getSingleResult();
    }

    @Override
    public List<Route> searchRoutes(Map<String, String> params, int page, int size) {
        return getRoutes(params, page, size);
    }

    @Override
    public long countRoutesByParams(Map<String, String> params) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Route> root = cq.from(Route.class);

        Join<Route, RouteVariant>[] variantJoinHolder = new Join[1];
        List<Predicate> predicates = buildPredicates(params, cb, root, variantJoinHolder);

        cq.select(cb.countDistinct(root)).where(predicates.toArray(new Predicate[0]));

        return session.createQuery(cq).getSingleResult();
    }

    @Override
    public List<RouteVariant> findRouteVariantsByRouteId(Long routeId) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<RouteVariant> cq = cb.createQuery(RouteVariant.class);
        Root<RouteVariant> root = cq.from(RouteVariant.class);

        cq.select(root).where(cb.equal(root.get("route").get("id"), routeId));

        return session.createQuery(cq).getResultList();
    }

    @Override
    public Route findById(Long id) {
        return getCurrentSession().get(Route.class, id);
    }

    @Override
    public void save(Route route) {
        getCurrentSession().persist(route);
    }

    @Override
    public void update(Route route) {
        getCurrentSession().merge(route);
    }

    @Override
    public void delete(Long id) {
        Session session = getCurrentSession();
        Route route = findById(id);
        if (route != null) {
            session.remove(route);
        }
    }

    private List<Route> getRoutes(Map<String, String> params, int page, int size) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Route> cq = cb.createQuery(Route.class);
        Root<Route> root = cq.from(Route.class);
        cq.select(root).distinct(true);

        Join<Route, RouteVariant>[] variantJoinHolder = new Join[1];
        List<Predicate> predicates = buildPredicates(params, cb, root, variantJoinHolder);

        cq.where(predicates.toArray(new Predicate[0]));

        Query<Route> query = session.createQuery(cq);

        if (page > 0 && size > 0) {
            int start = (page - 1) * size;
            query.setMaxResults(size);
            query.setFirstResult(start);
        }

        return query.getResultList();
    }

    private List<Predicate> buildPredicates(Map<String, String> params, CriteriaBuilder cb, Root<Route> root, Join<Route, RouteVariant>[] variantJoinHolder) {
        List<Predicate> predicates = new ArrayList<>();

        if (params == null || params.isEmpty()) {
            return predicates;
        }

        Join<Route, RouteVariant> variantJoin = null;
        if (params.containsKey("startStop") || params.containsKey("endStop")) {
            variantJoin = root.join("routeVariants", JoinType.INNER);
            variantJoinHolder[0] = variantJoin;
        }

        if (params.containsKey("name")) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + params.get("name").toLowerCase() + "%"));
        }
        if (params.containsKey("code")) {
            predicates.add(cb.like(cb.lower(root.get("code")), "%" + params.get("code").toLowerCase() + "%"));
        }
        if (params.containsKey("type")) {
            predicates.add(cb.like(cb.lower(root.get("type")), "%" + params.get("type").toLowerCase() + "%"));
        }
        if (params.containsKey("startStop") && variantJoin != null) {
            predicates.add(cb.like(cb.lower(variantJoin.get("startStop")), "%" + params.get("startStop").toLowerCase() + "%"));
        }
        if (params.containsKey("endStop") && variantJoin != null) {
            predicates.add(cb.like(cb.lower(variantJoin.get("endStop")), "%" + params.get("endStop").toLowerCase() + "%"));
        }

        return predicates;
    }
}