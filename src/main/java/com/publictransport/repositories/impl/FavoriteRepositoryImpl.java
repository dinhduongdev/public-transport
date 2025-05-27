package com.publictransport.repositories.impl;

import com.publictransport.models.Favorite;
import com.publictransport.repositories.FavoriteRepository;
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

import java.util.ArrayList;
import java.util.List;


@Repository
@Transactional
public class FavoriteRepositoryImpl implements FavoriteRepository {
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public Favorite save(Favorite favorite) {
        Session s = this.factory.getObject().getCurrentSession();
        if (favorite.getId() == null) {
            s.persist(favorite);
        } else {
            s.merge(favorite);
        }
        return favorite;
    }

    @Override
    public Favorite findById(Long id) {
        Session session = factory.getObject().getCurrentSession();
        return session.get(Favorite.class, id);
    }

    @Override
    public Favorite findByIdWithUser(Long id) {
        Session session = factory.getObject().getCurrentSession();
        return session.createQuery(
                        "FROM Favorite f JOIN FETCH f.user WHERE f.id = :id", Favorite.class)
                .setParameter("id", id)
                .uniqueResult();
    }

    @Override
    public List<Favorite> findByUserId(Long userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Favorite> q = b.createQuery(Favorite.class);
        Root<Favorite> root = q.from(Favorite.class);
        q.select(root);

        q.where(b.equal(root.get("user").get("id").as(Long.class), userId));

        Query query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public Favorite findByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, String targetType) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Favorite> q = b.createQuery(Favorite.class);
        Root<Favorite> root = q.from(Favorite.class);
        q.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(root.get("user").get("id").as(Long.class), userId));
        predicates.add(b.equal(root.get("targetId").as(Long.class), targetId));
        predicates.add(b.equal(root.get("targetType"), targetType));
        q.where(predicates.toArray(new Predicate[0]));

        Query query = s.createQuery(q);
        List<Favorite> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<Favorite> findAllByTargetIdAndTargetType(Long targetId, String targetType) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Favorite> query = builder.createQuery(Favorite.class);
        Root<Favorite> root = query.from(Favorite.class);
        query.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("targetId"), targetId));
        predicates.add(builder.equal(root.get("targetType"), targetType));

        query.where(predicates.toArray(new Predicate[0]));

        return session.createQuery(query).getResultList();
    }


    @Override
    public List<Favorite> findByUserIdAndTargetType(Long userId, String targetType) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Favorite> query = builder.createQuery(Favorite.class);
        Root<Favorite> root = query.from(Favorite.class);
        query.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("user").get("id"), userId));
        predicates.add(builder.equal(root.get("targetType"), targetType));

        query.where(predicates.toArray(new Predicate[0]));

        return session.createQuery(query).getResultList();
    }

    @Override
    public void deleteById(Long id) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Favorite> q = b.createQuery(Favorite.class);
        Root<Favorite> root = q.from(Favorite.class);
        q.select(root);

        q.where(b.equal(root.get("id").as(Long.class), id));

        Query query = s.createQuery(q);
        Favorite favorite = (Favorite) query.getSingleResult();
        if (favorite != null) {
            s.delete(favorite);
        }
    }

    @Override
    public List<Favorite> findByUserIdAndIsObserved(Long userId, Boolean isObserved) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Favorite> q = b.createQuery(Favorite.class);
        Root<Favorite> root = q.from(Favorite.class);
        q.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(b.equal(root.get("user").get("id").as(Long.class), userId));
        predicates.add(b.equal(root.get("isObserved"), isObserved));
        q.where(predicates.toArray(new Predicate[0]));

        Query query = s.createQuery(q);
        return query.getResultList();
    }

    @Override
    public void deleteFavorite(Long id) {
        Session session = factory.getObject().getCurrentSession();
        Favorite f = session.get(Favorite.class, id);
        if (f != null) {
            session.delete(f);
        }
    }
}
