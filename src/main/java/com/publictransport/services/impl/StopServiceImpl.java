package com.publictransport.services.impl;

import com.publictransport.dto.params.StationFilter;
import com.publictransport.models.Stop;
import com.publictransport.repositories.StopRepository;
import com.publictransport.services.StationService;
import com.publictransport.services.StopService;
import com.publictransport.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StopServiceImpl implements StopService {
    private StopRepository stopRepository;
    private StationService stationService;

    @Autowired
    public StopServiceImpl(StopRepository stopRepository, StationService stationService) {
        this.stopRepository = stopRepository;
        this.stationService = stationService;
    }

    @Override
    public List<Stop> findStopsByRouteVariantId(Long routeVariantId) {
        return stopRepository.findStopsByRouteVariantId(routeVariantId);
    }
    @Override
    public void save(Stop stop) {
        stopRepository.save(stop);
    }

    @Override
    public Optional<Stop> findById(Long id) {
        return stopRepository.findById(id);
    }

    @Override
    public void deleteByRouteVariantId(Long routeVariantId) {
        stopRepository.deleteByRouteVariantId(routeVariantId);
    }

    @Override
    public List<Stop> findNearbyStops(String coordsString, double radiusKm, int limit) {
        if (!MapUtils.isValidCoordinates(coordsString)) {
            return List.of();
        }
        // Tìm các trạm dừng gần một tọa độ nhất định trong bán kính nhất định
        StationFilter filter = new StationFilter();
        filter.setLatLng(coordsString);
        filter.setRadiusKm(radiusKm);
        filter.setSize(limit); // Lấy tất cả các trạm dừng trong bán kính
        return stationService.findStations(filter, true).stream()
                .flatMap(station -> station.getStops().stream())
                .collect(Collectors.toList());
    }
}
