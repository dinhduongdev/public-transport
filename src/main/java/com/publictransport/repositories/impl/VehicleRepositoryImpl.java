package com.publictransport.repositories.impl;

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
        if (vehicle.getId() == null) {
            s.persist(vehicle);
        } else {
            vehicle = (Vehicle) s.merge(vehicle);
        }
        return vehicle;
    }

    @Override
    public Vehicle getVehicleById(Long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(Vehicle.class, id);
    }

    @Override
    public void deleteVehicle(Long id) {
        Session s = this.factory.getObject().getCurrentSession();
        Vehicle vehicle = s.get(Vehicle.class, id);
        if (vehicle != null) {
            s.remove(vehicle);
        }
    }


}
