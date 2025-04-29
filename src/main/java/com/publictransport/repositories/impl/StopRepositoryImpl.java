/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.repositories.impl;

import com.publictransport.models.Route;
import com.publictransport.models.Station;
import com.publictransport.models.Stop;
import com.publictransport.repositories.StopRepository;
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
public class StopRepositoryImpl implements StopRepository {

    @Autowired
    LocalSessionFactoryBean factory;

    @Override
    public List<Stop> getStops() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Stop", Stop.class);
        return q.getResultList();
    }

    @Override
    public List<Stop> getStopsByRouteId(Long id) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Stop WHERE routeId.id = :routeId ORDER BY stopOrder", Stop.class);
        q.setParameter("routeId", id);
        return q.getResultList();
    }

    @Override
    public Stop saveStop(Stop stop) {
        Session s = this.factory.getObject().getCurrentSession();
        // Reattach the Route entity if it's detached
        if (stop.getRouteId() != null && stop.getRouteId().getId() != null) {
            stop.setRouteId((Route) s.merge(stop.getRouteId()));
        }
        // Reattach the Station entity if it's detached
        if (stop.getStationId() != null && stop.getStationId().getId() != null) {
            stop.setStationId((Station) s.merge(stop.getStationId()));
        }
        s.persist(stop);
        return stop;
    }

    @Override
    public void deleteStopsByRouteId(Long routeId) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("DELETE FROM Stop WHERE routeId.id = :routeId");
        q.setParameter("routeId", routeId);
        q.executeUpdate();
    }

}
