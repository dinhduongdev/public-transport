package com.publictransport.controllers;


import com.publictransport.models.Vehicle;
import com.publictransport.models.VehicleType;
import com.publictransport.services.VehicleService;
import com.publictransport.services.VehicleTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Transactional
public class VehicleController {
    @Autowired
    VehicleService vehicleService;
    @Autowired
    VehicleTypeService vehicleTypeService;

    @RequestMapping("/manage-vehicles")
    public String manageVehiclesType(Model model) {
        List<Vehicle> vehicles = vehicleService.getVehicles();
        List<VehicleType> vehicleTypes = vehicleTypeService.getVehicleTypes();
        System.out.println(vehicles);
        System.out.println(vehicleTypes);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("vehicleTypes", vehicleTypes);
        return "vehicles";
    }
}
