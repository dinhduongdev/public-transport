package com.publictransport.repositories;

import java.util.List;

public interface VehicleRepository {
    List<Vehicle> getVehicles();
    Vehicle saveVehicle(Vehicle vehicle);
    Vehicle getVehicleById(Long id);
    void deleteVehicle(Long id);
}
