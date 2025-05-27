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
    private final MapProxy mapProxy;

    @Autowired
    public APIStationController(StationService stationService, MapProxy mapProxy) {
        this.stationService = stationService;
        this.mapProxy = mapProxy;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStations(@Valid StationFilter params) {
        if (params.isKeywordSet()) {
            // nếu từ khóa được cung cấp, sử dụng proxy để tìm kiếm
            var optAddrAndCoords = mapProxy.getCoordinates(params.getKeyword());
            if (optAddrAndCoords.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy địa chỉ hoặc tọa độ cho từ khóa"));
            }
            var addrAndCoords = optAddrAndCoords.get();
            var newFilter = new StationFilter();
            newFilter.setLng(addrAndCoords.getRight().getLng());
            newFilter.setLat(addrAndCoords.getRight().getLat());
            newFilter.setRadiusKm(params.getRadiusKm());
            newFilter.setPage(params.getPage());
            newFilter.setSize(params.getSize());
            newFilter.setFormattedAddress(addrAndCoords.getLeft());
            params = newFilter;
        }

        long totalStations = stationService.countStations(params);
        int totalPages = (int) Math.ceil((double) totalStations / params.getSize());

        if (params.getPage() > totalPages) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy gì trên trang này"));
        }

        List<Station> stations = stationService.findStations(params);
        Map<String, Object> response = Map.of(
                "formattedAddress", params.getFormattedAddress(),
                "stations", stations,
                "totalStations", totalStations,
                "totalPages", totalPages,
                "currentPage", params.getPage(),
                "pageSize", params.getSize()
        );
        return ResponseEntity.ok(response);
    }

}
