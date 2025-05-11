package com.publictransport.repositories.impl;

import com.publictransport.models.Station;
import com.publictransport.repositories.StationRepository;
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
public class StationRepositoryImpl implements StationRepository {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private LocalSessionFactoryBean factory;

    private Session getCurrentSession() {
        return factory.getObject().getCurrentSession();
    }

    @Override
    public List<Station> findAllStations(int page, int size) {
        return getStations(null, page, size);
    }

    @Override
    public long countAllStations() {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Station> root = cq.from(Station.class);

        cq.select(cb.count(root));

        return session.createQuery(cq).getSingleResult();
    }

    @Override
    public List<Station> searchStations(Map<String, String> params, int page, int size) {
        return getStations(params, page, size);
    }

    @Override
    public long countStationsByParams(Map<String, String> params) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Station> root = cq.from(Station.class);

        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            if (params.containsKey("name")) {
                String namePattern = "%" + params.get("name").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), namePattern));
            }
            if (params.containsKey("address")) {
                String addressPattern = "%" + params.get("address").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("address")), addressPattern));
            }
            if (params.containsKey("street")) {
                String streetPattern = "%" + params.get("street").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("street")), streetPattern));
            }
            if (params.containsKey("ward")) {
                String wardPattern = "%" + params.get("ward").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("ward")), wardPattern));
            }
            if (params.containsKey("zone")) {
                String zonePattern = "%" + params.get("zone").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("zone")), zonePattern));
            }
        }

        cq.select(cb.count(root)).where(predicates.toArray(new Predicate[0]));

        return session.createQuery(cq).getSingleResult();
    }

    @Override
    public Station findById(Long id) {
        Session session = getCurrentSession();
        return session.get(Station.class, id);
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
        Session session = getCurrentSession();
        Station station = findById(id);
        if (station != null) {
            session.remove(station);
        }
    }

    private List<Station> getStations(Map<String, String> params, int page, int size) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Station> cq = cb.createQuery(Station.class);
        Root<Station> root = cq.from(Station.class);
        cq.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();
            if (params.containsKey("name")) {
                String namePattern = "%" + params.get("name").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), namePattern));
            }
            if (params.containsKey("address")) {
                String addressPattern = "%" + params.get("address").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("address")), addressPattern));
            }
            if (params.containsKey("street")) {
                String streetPattern = "%" + params.get("street").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("street")), streetPattern));
            }
            if (params.containsKey("ward")) {
                String wardPattern = "%" + params.get("ward").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("ward")), wardPattern));
            }
            if (params.containsKey("zone")) {
                String zonePattern = "%" + params.get("zone").toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("zone")), zonePattern));
            }
            cq.where(predicates.toArray(new Predicate[0]));
        }

        Query<Station> query = session.createQuery(cq);

        if (page > 0 && size > 0) {
            int start = (page - 1) * size;
            query.setMaxResults(size);
            query.setFirstResult(start);
        }

        return query.getResultList();
    }
}