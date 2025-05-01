package com.publictransport.services;

import com.publictransport.models.Vehicle;
import java.util.List;

public interface VehicleService {
    List<Vehicle> getVehicles();
    Vehicle saveVehicle(Vehicle vehicle);
    Vehicle getVehicleById(Long id);
    void deleteVehicle(Long id);
}
