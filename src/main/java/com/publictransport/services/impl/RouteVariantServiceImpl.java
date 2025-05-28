package com.publictransport.services.impl;

import com.publictransport.models.RouteVariant;
import com.publictransport.repositories.RouteVariantRepository;
import com.publictransport.services.RouteVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RouteVariantServiceImpl implements RouteVariantService {

    @Autowired
    private RouteVariantRepository routeVariantRepository;


    @Override
    public List<RouteVariant> findAllRouteVariants(int page, int size) {
        return routeVariantRepository.findAllRouteVariants(page, size);
    }

    @Override
    public long countAllRouteVariants() {
        return routeVariantRepository.countAllRouteVariants();
    }

    @Override
    public List<RouteVariant> searchRouteVariants(Map<String, String> params, int page, int size) {
        return routeVariantRepository.searchRouteVariants(params, page, size);
    }

    @Override
    public long countRouteVariantsByParams(Map<String, String> params) {
        return routeVariantRepository.countRouteVariantsByParams(params);
    }

    @Override
    public RouteVariant findById(Long id) {
        return routeVariantRepository.findById(id).orElseThrow();
    }

    @Override
    public void save(RouteVariant routeVariant) {
        routeVariantRepository.save(routeVariant);
    }

    @Override
    public void update(RouteVariant routeVariant) {
        routeVariantRepository.update(routeVariant);
    }

    @Override
    public void delete(Long id) {
        routeVariantRepository.delete(id);
    }

    @Override
    public List<RouteVariant> findByRouteId(Long routeId) {
        return routeVariantRepository.findByRouteId(routeId);
    }

    @Override
    public List<RouteVariant> findByStationId(Long stationId) {
        return routeVariantRepository.findByStationId(stationId);
    }

    @Override
    public Optional<Double> findMeanDistanceBetweenStops(Long routeVariantId) {
        return routeVariantRepository.findMeanDistanceBetweenStops(routeVariantId);
    }
}
