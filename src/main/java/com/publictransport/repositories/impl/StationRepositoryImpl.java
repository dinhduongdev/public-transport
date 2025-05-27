package com.publictransport.repositories.impl;

import com.publictransport.dto.params.StationFilter;
import com.publictransport.models.Station;
import com.publictransport.repositories.StationRepository;
import com.publictransport.utils.PaginationUtils;
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

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class StationRepositoryImpl implements StationRepository {

    private final SessionFactory factory;

    @Autowired
    public StationRepositoryImpl(SessionFactory factory) {
        this.factory = factory;
    }

    private Session getCurrentSession() {
        return factory.getCurrentSession();
    }

    @Override
    public Optional<Station> findById(Long id) {
        Session session = getCurrentSession();
        return Optional.ofNullable(session.get(Station.class, id));
    }

    @Override
    public void save(Station station) {
        Session session = getCurrentSession();
        session.persist(station);
    }

    @Override
    public void update(Station station) {
        Session session = getCurrentSession();
        session.merge(station);
    }

    @Override
    public void delete(Long id) {
        Session session = factory.getCurrentSession();
        Optional<Station> station = findById(id);
        station.ifPresent(session::remove);
    }

    @Override
    public List<Station> findStations(StationFilter filter) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Station> cq = cb.createQuery(Station.class);
        Root<Station> root = cq.from(Station.class);
        cq.select(root);

        List<Predicate> predicates = filter.toPredicateList(cb, root);

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }
        Query<Station> query = session.createQuery(cq);

        // Phân trang
        PaginationUtils.setQueryResultsRange(query, filter);
        return query.getResultList();
    }

    @Override
    public long countStations(StationFilter filter) {
        Session session = getCurrentSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Station> root = criteriaQuery.from(Station.class);
        criteriaQuery.select(criteriaBuilder.count(root));

        List<Predicate> predicates = filter.toPredicateList(criteriaBuilder, root);
        if (!predicates.isEmpty())
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        return session.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public List<Station> getAllStations() {
        Session session = getCurrentSession();
        return session.createQuery("FROM Station", Station.class).getResultList();
    }
}



