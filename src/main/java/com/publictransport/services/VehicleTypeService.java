package com.publictransport.services;

import com.publictransport.models.Vehicle;
import com.publictransport.models.VehicleType;

import java.util.List;

public interface VehicleTypeService {
    List<VehicleType> getVehicleTypes();
    VehicleType saveVehicleType(VehicleType vehicleType);
}
