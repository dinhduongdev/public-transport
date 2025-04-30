package com.publictransport.services.impl;

import com.publictransport.models.Vehicle;
import com.publictransport.models.VehicleType;
import com.publictransport.repositories.VehicleRepository;
import com.publictransport.repositories.VehicleTypeRepository;
import com.publictransport.services.VehicleTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleTypeServiceImpl implements VehicleTypeService {
    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    @Override
    public List<VehicleType> getVehicleTypes() {
        return this.vehicleTypeRepository.getVehicleTypes();
    }

    @Override
    public VehicleType saveVehicleType(VehicleType vehicleType) {
        return this.vehicleTypeRepository.saveVehicleType(vehicleType);
    }
}
