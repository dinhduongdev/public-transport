package com.publictransport.controllers;


import com.publictransport.services.VehicleService;
import com.publictransport.services.VehicleTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("vehicleTypes", vehicleTypes);
        return "vehicles";
    }
    @PostMapping("/manage-vehicles")
    public String addVehicle(@ModelAttribute("vehicle") Vehicle vehicle) {
        vehicleService.saveVehicle(vehicle);
        return "redirect:/manage-vehicles";
    }

    @RequestMapping("/manage-vehicles/detail/{id}")
    public String viewVehicleDetail(@PathVariable("id") Long id, Model model) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        if (vehicle == null) {
            return "redirect:/manage-vehicles";
        }
        model.addAttribute("vehicle", vehicle);
        return "vehicle-detail";
    }


    @RequestMapping("/manage-vehicles/delete/{id}")
    public String deleteVehicle(@PathVariable("id") Long id) {
        vehicleService.deleteVehicle(id);
        return "redirect:/manage-vehicles";
    }

    @RequestMapping("/manage-vehicles/edit/{id}")
    public String showEditVehicleForm(@PathVariable("id") Long id, Model model) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        if (vehicle == null) {
            return "redirect:/manage-vehicles";
        }
        List<VehicleType> vehicleTypes = vehicleTypeService.getVehicleTypes();
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("vehicleTypes", vehicleTypes);
        return "vehicle-edit";
    }

    @PostMapping("/manage-vehicles/edit")
    public String updateVehicle(@ModelAttribute("vehicle") Vehicle vehicle) {
        vehicleService.saveVehicle(vehicle);
        return "redirect:/manage-vehicles";
    }
}
