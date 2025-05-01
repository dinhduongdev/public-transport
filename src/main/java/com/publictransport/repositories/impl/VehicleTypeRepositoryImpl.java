package com.publictransport.repositories.impl;

import com.publictransport.models.VehicleType;
import com.publictransport.repositories.VehicleTypeRepository;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VehicleTypeRepositoryImpl implements VehicleTypeRepository {
    @Autowired
    LocalSessionFactoryBean factory;

    @Override
    public List<VehicleType> getVehicleTypes() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM VehicleType", VehicleType.class);
        return q.getResultList();
    }

    @Override
    public VehicleType saveVehicleType(VehicleType vehicleType) {
        Session s = this.factory.getObject().getCurrentSession();
        if (vehicleType.getId() == null) {
            s.persist(vehicleType);
        } else {
            vehicleType = (VehicleType) s.merge(vehicleType);
        }
        return vehicleType;
    }

    @Override
    public VehicleType getVehicleTypeById(Long id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(VehicleType.class, id);
    }

    @Override
    public void deleteVehicleType(Long id) {
        Session s = this.factory.getObject().getCurrentSession();
        VehicleType vehicleType = s.get(VehicleType.class, id);
        if (vehicleType != null) {
            s.remove(vehicleType);
        }
    }
}
