package com.publictransport.services.impl;

import com.publictransport.models.Vehicle;
import com.publictransport.repositories.VehicleRepository;
import com.publictransport.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public List<Vehicle> getVehicles() {
        return this.vehicleRepository.getVehicles();
    }

    @Override
    public Vehicle saveVehicle(Vehicle vehicle) {
        return this.vehicleRepository.saveVehicle(vehicle);
    }

    @Override
    public Vehicle getVehicleById(Long id) {
        return this.vehicleRepository.getVehicleById(id);
    }

    @Override
    public void deleteVehicle(Long id) {
        this.vehicleRepository.deleteVehicle(id);
    }
}
