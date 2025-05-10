/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.repositories.impl;

import com.publictransport.repositories.StationRepository;
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
public class StationRepositoryImpl implements StationRepository{
    @Autowired
    LocalSessionFactoryBean factory;
    @Override
    public List<Station> getStations() {
        Session s = this.factory.getObject().getCurrentSession();
        Query  q = s.createQuery("FROM Station", Station.class);
        return q.getResultList();
    }

    @Override
    public Station getStationById(Long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Station.class, id);
    }
    
    
    
}
