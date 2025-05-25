package com.publictransport.repositories.impl;

import com.publictransport.models.Rating;
import com.publictransport.repositories.RatingRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class RatingRepositoryImpl implements RatingRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public Rating save(Rating rating) {
        Session s = this.factory.getObject().getCurrentSession();
        if (rating.getId() == null) {
            s.persist(rating);
        } else {
            s.merge(rating);
        }
        return rating;
    }

    @Override
    public List<Rating> findByRouteId(Long routeId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Rating> q = b.createQuery(Rating.class);
        Root root = q.from(Rating.class);
        q.select(root);

        q.where(b.equal(root.get("route").get("id").as(Long.class), routeId));

        Query query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public Double getAverageScoreByRouteId(Long routeId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Double> q = b.createQuery(Double.class);
        Root root = q.from(Rating.class);

        q.select(b.avg(root.get("score")));
        q.where(b.equal(root.get("route").get("id").as(Long.class), routeId));

        Query query = s.createQuery(q);
        Double result = (Double) query.getSingleResult();
        return result != null ? result : 0.0;
    }

    @Override
    public Long countByRouteId(Long routeId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root root = q.from(Rating.class);

        q.select(b.count(root));
        q.where(b.equal(root.get("route").get("id").as(Long.class), routeId));

        Query query = s.createQuery(q);
        return (Long) query.getSingleResult();
    }

    @Override
    public Map<Integer, Long> countRatingsByScore(Long routeId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Object[]> q = b.createQuery(Object[].class);
        Root root = q.from(Rating.class);

        q.multiselect(root.get("score"), b.count(root));
        q.where(b.equal(root.get("route").get("id").as(Long.class), routeId));
        q.groupBy(root.get("score"));

        Query query = s.createQuery(q);
        List<Object[]> results = query.getResultList();

        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingDistribution.put(i, 0L);
        }

        for (Object[] result : results) {
            Integer score = (Integer) result[0];
            Long count = (Long) result[1];
            ratingDistribution.put(score, count);
        }

        return ratingDistribution;
    }

    @Override
    public Rating findByUserIdAndRouteId(Long userId, Long routeId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Rating> q = b.createQuery(Rating.class);
        Root root = q.from(Rating.class);
        q.select(root);

        Predicate userPredicate = b.equal(root.get("user").get("id").as(Long.class), userId);
        Predicate routePredicate = b.equal(root.get("route").get("id").as(Long.class), routeId);
        q.where(b.and(userPredicate, routePredicate));

        Query query = s.createQuery(q);
        List<Rating> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}