package com.publictransport.controllers;

import com.publictransport.models.Station;
import com.publictransport.services.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StationController {

    @Autowired
    private StationService stationService;

    @GetMapping("/manage-stations")
    public String getAllStations(Model model,
                                 @RequestParam(name = "page", defaultValue = "1") int page,
                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                 @RequestParam(name = "name", required = false) String name,
                                 @RequestParam(name = "address", required = false) String address,
                                 @RequestParam(name = "street", required = false) String street,
                                 @RequestParam(name = "ward", required = false) String ward,
                                 @RequestParam(name = "zone", required = false) String zone) {
        List<Station> stations;
        long totalItems;
        int totalPages;

        // Tạo params để truyền vào phương thức tìm kiếm
        Map<String, String> params = new HashMap<>();
        if (name != null && !name.trim().isEmpty()) {
            params.put("name", name);
        }
        if (address != null && !address.trim().isEmpty()) {
            params.put("address", address);
        }
        if (street != null && !street.trim().isEmpty()) {
            params.put("street", street);
        }
        if (ward != null && !ward.trim().isEmpty()) {
            params.put("ward", ward);
        }
        if (zone != null && !zone.trim().isEmpty()) {
            params.put("zone", zone);
        }

        // Nếu có tham số tìm kiếm, gọi phương thức tìm kiếm
        if (!params.isEmpty()) {
            stations = stationService.searchStations(params, page, size);
            totalItems = stationService.countStationsByParams(params);
            totalPages = (int) Math.ceil((double) totalItems / size);
        } else {
            // Nếu không có tham số, lấy toàn bộ danh sách
            stations = stationService.findAllStations(page, size);
            totalItems = stationService.countAllStations();
            totalPages = (int) Math.ceil((double) totalItems / size);
        }

        model.addAttribute("stations", stations);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("size", size);
        model.addAttribute("name", name);
        model.addAttribute("address", address);
        model.addAttribute("street", street);
        model.addAttribute("ward", ward);
        model.addAttribute("zone", zone);
        return "stations";
    }

    @GetMapping("/manage-stations/detail/{id}")
    public String viewStationDetail(@PathVariable("id") Long id, Model model) {
        Station station = stationService.findById(id);
        if (station == null) {
            model.addAttribute("msg", "Trạm không tồn tại.");
            return "redirect:/manage-stations";
        }
        model.addAttribute("station", station);
        return "station-detail";
    }

    @GetMapping("/manage-stations/add")
    public String showAddStationForm(Model model) {
        model.addAttribute("station", new Station());
        return "station-add";
    }

    @PostMapping("/manage-stations/add")
    public String addStation( Station station) {
        stationService.save(station);
        return "redirect:/manage-stations";
    }

    @GetMapping("/manage-stations/edit/{id}")
    public String showEditStationForm(@PathVariable("id") Long id, Model model) {
        Station station = stationService.findById(id);
        if (station == null) {
            model.addAttribute("msg", "Trạm không tồn tại.");
            return "redirect:/manage-stations";
        }
        model.addAttribute("station", station);
        return "station-edit";
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