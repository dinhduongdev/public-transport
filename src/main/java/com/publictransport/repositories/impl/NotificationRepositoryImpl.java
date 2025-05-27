package com.publictransport.repositories.impl;

import com.publictransport.dto.NotificationDTO;
import com.publictransport.models.Notification;
import com.publictransport.repositories.NotificationRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@Transactional
public class NotificationRepositoryImpl implements NotificationRepository {
    @Autowired
    private LocalSessionFactoryBean factory;


    @Override
    public Notification save(Notification notification) {
        Session session = factory.getObject().getCurrentSession();
        if (notification.getId() == null) {
            session.persist(notification);
        } else {
            session.merge(notification);
        }
        return notification;
    }

    @Override
    public List<NotificationDTO> findByUserId(Long userId) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<NotificationDTO> query = builder.createQuery(NotificationDTO.class);
        Root<Notification> root = query.from(Notification.class);

        query.select(builder.construct(
                NotificationDTO.class,
                root.get("id"),
                root.get("title"),
                root.get("message"),
                root.get("createdAt"),
                root.get("isRead"),
                root.get("user").get("id")
        ));
        // Điều kiện where
        query.where(builder.equal(root.get("user").get("id").as(Long.class), userId));

        return session.createQuery(query).getResultList();
    }

    @Override
    public Notification findById(Long id) {
        Session session = factory.getObject().getCurrentSession();
        return session.get(Notification.class, id);
    }

    @Override
    public NotificationDTO findDTOById(Long id) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<NotificationDTO> query = builder.createQuery(NotificationDTO.class);
        Root<Notification> root = query.from(Notification.class);

        query.select(builder.construct(
                NotificationDTO.class,
                root.get("id"),
                root.get("title"),
                root.get("message"),
                root.get("createdAt"),
                root.get("isRead"),
                root.get("user").get("id")
        ));

        query.where(builder.equal(root.get("id").as(Long.class), id));

        return session.createQuery(query).uniqueResult();
    }
}
