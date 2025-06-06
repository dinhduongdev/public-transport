package com.publictransport.repositories.impl;

import com.publictransport.dto.params.StationFilter;
import com.publictransport.models.*;
import com.publictransport.repositories.StationRepository;
import com.publictransport.utils.PaginationUtils;
import jakarta.persistence.criteria.*;
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
    public Optional<Station> findDuplicate(Station station) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Station> cq = cb.createQuery(Station.class);
        Root<Station> root = cq.from(Station.class);
        cq.select(root);

        // Truy cập các trường trong @Embedded
        Path<Double> latitudePath = root.get("coordinates").get("lat");
        Path<Double> longitudePath = root.get("coordinates").get("lng");

        Path<String> addressPath = root.get("location").get("address");
        Path<String> streetPath = root.get("location").get("street");
        Path<String> wardPath = root.get("location").get("ward");
        Path<String> zonePath = root.get("location").get("zone");

        Predicate nameMatch = cb.equal(root.get("name"), station.getName());
        Predicate latMatch = cb.equal(latitudePath, station.getCoordinates().getLat());
        Predicate lngMatch = cb.equal(longitudePath, station.getCoordinates().getLng());
        Predicate addressMatch = cb.equal(addressPath, station.getLocation().getAddress());
        Predicate streetMatch = cb.equal(streetPath, station.getLocation().getStreet());
        Predicate wardMatch = cb.equal(wardPath, station.getLocation().getWard());
        Predicate zoneMatch = cb.equal(zonePath, station.getLocation().getZone());

        cq.where(cb.and(nameMatch, latMatch, lngMatch, addressMatch, streetMatch, wardMatch, zoneMatch));

        return session.createQuery(cq).uniqueResultOptional();
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
    public List<Station> findStations(StationFilter filter, boolean fetchStops) {
        if (!fetchStops) {
            return findStations(filter);
        }
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();

        // lấy id của các trạm
        CriteriaQuery<Long> idQuery = cb.createQuery(Long.class);
        Root<Station> idRoot = idQuery.from(Station.class);
        idQuery.select(idRoot.get(Station_.id)).distinct(true);
        List<Predicate> predicates = filter.toPredicateList(cb, idRoot);
        if (!predicates.isEmpty()) {
            idQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        Query<Long> query = session.createQuery(idQuery);
        // Phân trang
        PaginationUtils.setQueryResultsRange(query, filter);
        List<Long> stationIds = query.getResultList();

        if (stationIds.isEmpty()) return List.of();

        // lấy các trạm với id đã tìm được với join fetch stops
        CriteriaQuery<Station> stationQuery = cb.createQuery(Station.class);
        Root<Station> stationRoot = stationQuery.from(Station.class);
        stationQuery.select(stationRoot);
        stationQuery.where(stationRoot.get(Station_.id).in(stationIds));
        // fetch het cho oach
        stationRoot.fetch(Station_.stops).fetch(Stop_.routeVariant).fetch(RouteVariant_.route);
        Query<Station> stationHibernateQuery = session.createQuery(stationQuery);
        return stationHibernateQuery.getResultList();
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

    public List<Long> getStationIds(StationFilter filter) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Station> root = cq.from(Station.class);
        cq.select(root.get(Station_.id));

        List<Predicate> predicates = filter.toPredicateList(cb, root);
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        Query<Long> query = session.createQuery(cq);
        PaginationUtils.setQueryResultsRange(query, filter);
        return query.getResultList();
    }

    public List<Station> getStationsByIds(List<Long> ids, boolean fetchStops) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Station> cq = cb.createQuery(Station.class);
        Root<Station> root = cq.from(Station.class);
        cq.select(root).where(root.get(Station_.id).in(ids));
        if (fetchStops) {
            root.fetch(Station_.stops).fetch(Stop_.routeVariant).fetch(RouteVariant_.route);
        }
        return session.createQuery(cq).getResultList();
    }
}



