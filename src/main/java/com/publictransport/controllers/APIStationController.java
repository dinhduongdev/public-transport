package com.publictransport.controllers;

import com.publictransport.dto.params.StationFilter;
import com.publictransport.models.Station;
import com.publictransport.proxies.MapProxy;
import com.publictransport.services.StationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stations")
@CrossOrigin
public class APIStationController {

    private final StationService stationService;

    @Autowired
    public APIStationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStations(@Valid StationFilter params) {
        // nếu từ khóa được cung cấp, sử dụng proxy để tìm kiếm toa do
        if (params.isKeywordSet()) {
            var optNewFilter = stationService.buildNewFilterByKeyword(params);
            if (optNewFilter.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy"));
            }
            params = optNewFilter.get();
        }

        long totalStations = stationService.countStations(params);
        int totalPages = (int) Math.ceil((double) totalStations / params.getSize());

        if (params.getPage() > totalPages) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy"));
        }

        List<Station> stations = stationService.findStations(params);

        Map<String, Object> response = Map.of(
                "formattedAddress", params.getFormattedAddress() != null ? params.getFormattedAddress() : "",
                "stations", stations,
                "totalStations", totalStations,
                "totalPages", totalPages,
                "currentPage", params.getPage(),
                "pageSize", params.getSize()
        );
        return ResponseEntity.ok(response);
    }

}
