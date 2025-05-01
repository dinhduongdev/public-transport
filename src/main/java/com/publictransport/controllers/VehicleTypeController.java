package com.publictransport.controllers;


import com.publictransport.models.VehicleType;
import com.publictransport.services.VehicleTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Transactional
public class VehicleTypeController {
    @Autowired
    VehicleTypeService vehicleTypeService;



    @RequestMapping("/manage-vehicles-type")
    public String manageVehiclesType(Model model) {
        List<VehicleType> vehicleTypes = vehicleTypeService.getVehicleTypes();
        model.addAttribute("vehicleTypes", vehicleTypes);
        model.addAttribute("vehicle_type", new VehicleType());
        return "vehicles-type";
    }

    @PostMapping("/manage-vehicles-type")
    public String addVehiclesType(Model model, @ModelAttribute(value = "vehicle_type") VehicleType vehicleType) {
        this.vehicleTypeService.saveVehicleType(vehicleType);
        return "redirect:/manage-vehicles-type";
    }
    @RequestMapping("/manage-vehicles-type/detail/{id}")
    public String viewVehicleTypeDetail(@PathVariable("id") Long id, Model model) {
        VehicleType vehicleType = vehicleTypeService.getVehicleTypeById(id);
        if (vehicleType == null) {
            return "redirect:/manage-vehicles-type";
        }
        model.addAttribute("vehicleType", vehicleType);
        return "vehicle-type-detail";
    }

    @RequestMapping("/manage-vehicles-type/edit/{id}")
    public String showEditVehicleTypeForm(@PathVariable("id") Long id, Model model) {
        VehicleType vehicleType = vehicleTypeService.getVehicleTypeById(id);
        if (vehicleType == null) {
            return "redirect:/manage-vehicles-type";
        }
        model.addAttribute("vehicleType", vehicleType);
        return "vehicle-type-edit";
    }

    @PostMapping("/manage-vehicles-type/edit")
    public String updateVehicleType(@ModelAttribute("vehicleType") VehicleType vehicleType) {
        vehicleTypeService.saveVehicleType(vehicleType);
        return "redirect:/manage-vehicles-type";
    }

    @RequestMapping("/manage-vehicles-type/delete/{id}")
    public String deleteVehicleType(@PathVariable("id") Long id) {
        vehicleTypeService.deleteVehicleType(id);
        return "redirect:/manage-vehicles-type";
    }
}
