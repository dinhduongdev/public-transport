package com.publictransport.repositories.impl;

import com.publictransport.models.Vehicle;
import com.publictransport.models.VehicleType;
import com.publictransport.repositories.VehicleRepository;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VehicleRepositoryImpl implements VehicleRepository {
    @Autowired
    LocalSessionFactoryBean factory;

    @Override
    public List<Vehicle> getVehicles() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM Vehicle", Vehicle.class);
        return q.getResultList();
    }
    @Override
    public Vehicle saveVehicle(Vehicle vehicle) {
        Session s = this.factory.getObject().getCurrentSession();
        s.persist(vehicle);
        return vehicle;
    }
}
