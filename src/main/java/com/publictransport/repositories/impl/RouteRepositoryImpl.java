package com.publictransport.repositories.impl;

import com.publictransport.models.Route;
import com.publictransport.models.RouteVariant;
import com.publictransport.repositories.RouteRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class RouteRepositoryImpl implements RouteRepository {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private LocalSessionFactoryBean factory;

    private Session getCurrentSession() {
        return factory.getObject().getCurrentSession();
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

        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            if (params.containsKey("name")) {
                String namePattern = "%" + params.get("name").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), namePattern));
            }
            if (params.containsKey("code")) {
                String codePattern = "%" + params.get("code").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("code")), codePattern));
            }
            if (params.containsKey("type")) {
                String typePattern = "%" + params.get("type").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("type").get("name")), typePattern));
            }
        }

        cq.select(cb.count(root)).where(predicates.toArray(new Predicate[0]));

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
        Session session = getCurrentSession();
        return session.get(Route.class, id);
    }

    @Override
    public void save(Route route) {
        Session session = getCurrentSession();
        session.persist(route);
    }

    @Override
    public void update(Route route) {
        Session session = getCurrentSession();
        session.merge(route);
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
        cq.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            if (params.containsKey("name")) {
                String namePattern = "%" + params.get("name").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), namePattern));
            }
            if (params.containsKey("code")) {
                String codePattern = "%" + params.get("code").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("code")), codePattern));
            }
            if (params.containsKey("type")) {
                String typePattern = "%" + params.get("type").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("type").get("name")), typePattern));
            }
            cq.where(predicates.toArray(new Predicate[0]));
        }

        Query<Route> query = session.createQuery(cq);

        if (page > 0 && size > 0) {
            int start = (page - 1) * size;
            query.setMaxResults(size);
            query.setFirstResult(start);
        }

        return query.getResultList();
    }
}