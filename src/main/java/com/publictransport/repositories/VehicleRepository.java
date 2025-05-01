package com.publictransport.repositories;

import com.publictransport.models.Vehicle;
import java.util.List;

public interface VehicleRepository {
    List<Vehicle> getVehicles();
    Vehicle saveVehicle(Vehicle vehicle);
    Vehicle getVehicleById(Long id);
    void deleteVehicle(Long id);
}
