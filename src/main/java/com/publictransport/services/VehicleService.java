package com.publictransport.services;

import com.publictransport.models.Vehicle;
import com.publictransport.models.VehicleType;

import java.util.List;

public interface VehicleService {
    List<Vehicle> getVehicles();
    Vehicle saveVehicle(Vehicle vehicle);
}
