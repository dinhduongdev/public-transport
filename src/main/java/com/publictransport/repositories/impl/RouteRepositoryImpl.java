//package com.publictransport.repositories.impl;
//
//import com.publictransport.models.Route;
//import com.publictransport.models.RouteVariant;
//import com.publictransport.repositories.RouteRepository;
//import jakarta.persistence.Query;
//import org.hibernate.Session;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.List;
//
//@Repository
//@Transactional
//public class RouteRepositoryImpl implements RouteRepository {
//    private static final int PAGE_SIZE = 10;
//    @Autowired
//    private LocalSessionFactoryBean factory;
//
//    private Session getCurrentSession() {
//        return factory.getObject().getCurrentSession();
//    }
//
//    @Override
//    public List<Route> findAllRoutes(int page, int size) {
//        Session session = getCurrentSession();
//        Query q = session.createQuery("FROM Route", Route.class);
//        q.setFirstResult((page - 1) * size); // Bắt đầu từ index
//        q.setMaxResults(size); // Số lượng mỗi trang
//        return q.getResultList();
//    }
//
//    @Override
//    public long countAllRoutes() {
//        Session session = getCurrentSession();
//        Query  q = session.createQuery("SELECT COUNT(r.id) FROM Route r", Long.class);
//        return (long) q.getSingleResult();
//    }
//
//    @Override
//    public List<RouteVariant> findRouteVariantsByRouteId(Long routeId) {
//        Session s = getCurrentSession();
//        Query q = s.createQuery("FROM RouteVariant rv WHERE rv.route.id = :routeId", RouteVariant.class);
//        q.setParameter("routeId", routeId);
//        return q.getResultList();
//    }
//
//    @Override
//    public Route findById(Long id) {
//        Session s = getCurrentSession();
//        return s.get(Route.class, id);
//    }
//
//    @Override
//    public void save(Route route) {
//        Session s = getCurrentSession();
//        s.save(route);
//    }
//
//    @Override
//    public void update(Route route) {
//        Session s = getCurrentSession();
//        s.update(route);
//    }
//
//    @Override
//    public void delete(Long id) {
//        Session s = getCurrentSession();
//        Route route = s.get(Route.class, id);
//        if (route != null) {
//            s.delete(route);
//        }
//    }
//
//    @Override
//    public List<Route> searchRoutes(String keyword, int page, int size) {
//        Session session = getCurrentSession();
//        String hql = "FROM Route r WHERE r.name LIKE :keyword OR r.code LIKE :keyword";
//        Query<Route> query = session.createQuery(hql, Route.class);
//        query.setParameter("keyword", "%" + (keyword != null ? keyword : "") + "%");
//        query.setFirstResult((page - 1) * size);
//        query.setMaxResults(size);
//        return query.getResultList();
//    }
//
//    @Override
//    public long countRoutesByKeyword(String keyword) {
//        return 0;
//    }
//}

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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            String keyword = params.get("keyword");
            if (keyword != null && !keyword.isEmpty()) {
                String keywordPattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), keywordPattern),
                        cb.like(cb.lower(root.get("code")), keywordPattern)
                ));
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

            String keyword = params.get("keyword");
            if (keyword != null && !keyword.isEmpty()) {
                String keywordPattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), keywordPattern),
                        cb.like(cb.lower(root.get("code")), keywordPattern)
                ));
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