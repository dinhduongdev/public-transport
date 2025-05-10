package com.publictransport.repositories;

import java.util.List;

public interface VehicleTypeRepository {
    List<VehicleType> getVehicleTypes();
    VehicleType saveVehicleType(VehicleType vehicleType);
    VehicleType getVehicleTypeById(Long id);
    void deleteVehicleType(Long id);
}
