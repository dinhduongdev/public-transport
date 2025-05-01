/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.repositories.impl;

import com.publictransport.models.Route;
import com.publictransport.repositories.RouteRepository;
import jakarta.persistence.Query;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author duong
 */
@Repository
@Transactional
public class RouteRepositoryImpl implements RouteRepository{
    @Autowired
    LocalSessionFactoryBean factory;

    private Session getCurrentSession() {
        return factory.getObject().getCurrentSession();
    }


    @Override
    public List<Route> getRoutes() {
        Session s = getCurrentSession();
        Query  q = s.createQuery("FROM Route", Route.class);
        return q.getResultList();
    }

    @Override
    public Route saveRoute(Route route) {
        Session s = getCurrentSession();
        s.persist(route);
        return route;
    }

    @Override
    public Route getRouteById(Long id) {
        Session s = getCurrentSession();
        return s.get(Route.class, id);
    }

    @Override
    public void deleteRoute(Long id) {
        Session s = getCurrentSession();
        Query q = s.createQuery("DELETE FROM Route WHERE id = :id");
        q.setParameter("id", id);
        q.executeUpdate();
    }
}
