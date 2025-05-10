package com.publictransport.services;

import java.util.List;

public interface VehicleTypeService {
    List<VehicleType> getVehicleTypes();
    VehicleType saveVehicleType(VehicleType vehicleType);
    VehicleType getVehicleTypeById(Long id);
    void deleteVehicleType(Long id);
}
