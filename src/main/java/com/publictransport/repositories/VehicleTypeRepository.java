package com.publictransport.repositories;

import com.publictransport.models.VehicleType;

import java.util.List;

public interface VehicleTypeRepository {
    List<VehicleType> getVehicleTypes();
    VehicleType saveVehicleType(VehicleType vehicleType);
}
