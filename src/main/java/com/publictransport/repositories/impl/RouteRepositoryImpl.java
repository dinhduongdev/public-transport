package com.publictransport.repositories.impl;

import com.publictransport.dto.params.RouteFilter;
import com.publictransport.models.Route;
import com.publictransport.models.Route_;
import com.publictransport.repositories.RouteRepository;
import com.publictransport.utils.PaginationUtils;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class RouteRepositoryImpl implements RouteRepository {

    private final SessionFactory factory;

    @Autowired
    public RouteRepositoryImpl(SessionFactory factory) {
        this.factory = factory;
    }

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    @Override
    public Optional<Route> findById(Long id) {
        Session session = factory.getCurrentSession();
        return Optional.ofNullable(session.get(Route.class, id));
    }

    @Override
    public Optional<Route> findById(Long id, boolean fetchRouteVariants) {
        if (!fetchRouteVariants) {
            return findById(id);
        }
        Session session = factory.getCurrentSession();
        String hql = "FROM Route r LEFT JOIN FETCH r.routeVariants WHERE r.id = :id";
        Query<Route> query = session.createQuery(hql, Route.class);
        return query.setParameter("id", id).uniqueResultOptional();
    }

    @Override
    public void save(Route route) {
        factory.getCurrentSession().persist(route);
    }

    @Override
    public void update(Route route) {
        factory.getCurrentSession().merge(route);
    }

    @Override
    public void delete(Long id) {
        Session session = factory.getCurrentSession();
        Optional<Route> route = findById(id);
        route.ifPresent(session::remove);
    }

    // hàm tìm không fetch routeVariants
    @Override
    public List<Route> findRoutes(RouteFilter filter) {
        Session session = factory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Route> cq = cb.createQuery(Route.class);
        Root<Route> root = cq.from(Route.class);
        cq.select(root);

        List<Predicate> predicates = filter.toPredicateList(cb, root);
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }
        Query<Route> query = session.createQuery(cq);

        // Phân trang
        PaginationUtils.setQueryResultsRange(query, filter);
        return query.getResultList();
    }


    // Sinh ra 2 truy vấn nếu fetchRouteVariants = true:
    // 1. Truy vấn lấy danh sách các Route ID theo filter (có phân trang)
    // 2. Truy vấn lấy Route với các routeVar theo danh sách ID của Route
    // Lý do: Nếu 1 truy vấn thì hibernate sẽ fetch hết và áp dụng phân trang sau (trong lớp ứng dụng)
    @Override
    public List<Route> findRoutes(RouteFilter filter, boolean fetchRouteVariants) {
        if (!fetchRouteVariants) {
            return findRoutes(filter);
        }
        Session session = factory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();

        // ======== 1. Truy vấn lấy danh sách các Route ID theo filter (có phân trang) ========
        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<Route> idRoot = idQuery.from(Route.class);
        idQuery.select(idRoot.get(Route_.id)).distinct(true);
        List<Predicate> predicates = filter.toPredicateList(cb, idRoot);
        if (!predicates.isEmpty()) {
            idQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        Query<Long> idHibernateQuery = session.createQuery(idQuery);

        // Phân trang
        PaginationUtils.setQueryResultsRange(idHibernateQuery, filter);
        List<Long> routeIds = idHibernateQuery.getResultList();

        if (routeIds.isEmpty()) return new ArrayList<>();

        // ======== 2. Truy vấn lấy Route với các routeVar theo danh sách ID của Route ========
        CriteriaQuery<Route> cq = cb.createQuery(Route.class);
        Root<Route> root = cq.from(Route.class);

        // Lấy routeVariants, join với RouteVariant
        root.fetch(Route_.routeVariants, JoinType.LEFT);
        cq.select(root);
        cq.where(root.get(Route_.id).in(routeIds));
        Query<Route> query = session.createQuery(cq);
        return query.getResultList();
    }

    @Override
    public List<Route> getAllRoutes() {
        Session session = factory.getCurrentSession();
        return session.createQuery("FROM Route", Route.class).getResultList();
    }

    @Override
    public Long countRoutes(RouteFilter filter) {
        Session session = factory.getCurrentSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Route> root = criteriaQuery.from(Route.class);

        criteriaQuery.select(criteriaBuilder.countDistinct(root));

        List<Predicate> predicates = filter.toPredicateList(criteriaBuilder, root);
        if (!predicates.isEmpty())
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        return session.createQuery(criteriaQuery).getSingleResult();
    }
}

