package com.publictransport.repositories.impl;

import com.publictransport.models.RouteVariant;
import com.publictransport.repositories.RouteVariantRepository;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class RouteVariantRepositoryImpl implements RouteVariantRepository {
    @Autowired
    LocalSessionFactoryBean factory;

    private Session getCurrentSession() {
        return factory.getObject().getCurrentSession();
    }

    @Override
    public List<RouteVariant> findAllRouteVariants(int page, int size) {
        Session s = getCurrentSession();
        Query q = s.createQuery("FROM RouteVariant", RouteVariant.class);
        q.setFirstResult((page - 1) * size); // Bắt đầu từ index
        q.setMaxResults(size); // Số lượng mỗi trang
        return q.getResultList();
    }

    @Override
    public Long countRouteVariants() {
        Session s = getCurrentSession();
        Query q = s.createQuery("SELECT COUNT(rv.id) FROM RouteVariant rv");
        return (long) q.getSingleResult();
    }


}
