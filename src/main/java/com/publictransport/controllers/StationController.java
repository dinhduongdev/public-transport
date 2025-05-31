package com.publictransport.controllers;

import com.publictransport.dto.params.StationFilter;
import com.publictransport.models.Station;
import com.publictransport.services.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class StationController {

    @Autowired
    private StationService stationService;

    @GetMapping("/manage-stations")
    public String getAllStations(Model model, StationFilter params, RedirectAttributes redirectAttributes) {
        long totalStations = stationService.countStations(params);
        int totalPages = (int) Math.ceil((double) totalStations / params.getSize());

        if (params.getPage() > totalPages) {
            redirectAttributes.addFlashAttribute("errorMsg", "Trang không tồn tại.");
            return "redirect:/manage-stations";
        }

        List<Station> stations = stationService.findStations(params);

        model.addAttribute("stations", stations);
        model.addAttribute("currentPage", params.getPage());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalStations);
        model.addAttribute("size", params.getSize());
        model.addAttribute("name", params.getName());
        model.addAttribute("address", params.getAddress());
        model.addAttribute("street", params.getStreet());
        model.addAttribute("ward", params.getWard());
        model.addAttribute("zone", params.getZone());
        return "station/stations";
    }

    @GetMapping("/manage-stations/detail/{id}")
    public String viewStationDetail(@PathVariable("id") Long id, Model model) {
        Optional<Station> optStation = stationService.findById(id);
        if (optStation.isEmpty()) {
            model.addAttribute("msg", "Trạm không tồn tại.");
            return "redirect:/manage-stations";
        }
        model.addAttribute("station", optStation.get());
        return "station/station-detail";
    }

    @GetMapping("/manage-stations/add")
    public String showAddStationForm(Model model) {
        model.addAttribute("station", new Station());
        return "station/station-add";
    }

    @PostMapping("/manage-stations/add")
    public String addStation(Station station, RedirectAttributes redirectAttributes) {
        try {
            stationService.save(station);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/manage-stations/add";
        }
        return "redirect:/manage-stations";
    }

    @GetMapping("/manage-stations/edit/{id}")
    public String showEditStationForm(@PathVariable("id") Long id, Model model) {
        Optional<Station> optStation = stationService.findById(id);
        if (optStation.isEmpty()) {
            model.addAttribute("msg", "Trạm không tồn tại.");
            return "redirect:/manage-stations";
        }
        model.addAttribute("station", optStation.get());
        return "station/station-edit";
    }

    @PostMapping("/manage-stations/edit/{id}")
    public String updateStation(@PathVariable("id") Long id, Station station) {
        station.setId(id);
        stationService.update(station);
        return "redirect:/manage-stations";
    }

    @GetMapping("/manage-stations/delete/{id}")
    public String deleteStation(@PathVariable("id") Long id) {
        stationService.delete(id);
        return "redirect:/manage-stations";
    }
}