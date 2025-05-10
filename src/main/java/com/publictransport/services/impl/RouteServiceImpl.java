package com.publictransport.services.impl;

import com.publictransport.models.Route;
import com.publictransport.models.RouteVariant;
import com.publictransport.repositories.RouteRepository;
import com.publictransport.services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RouteServiceImpl implements RouteService {
    @Autowired
    private RouteRepository routeRepository;


    @Override
    public List<Route> getAllRoutes(int page, int size) {
        return routeRepository.findAllRoutes(page, size);
    }

    @Override
    public long getTotalRoutes() {
        return routeRepository.countAllRoutes();
    }

    @Override
    public int getTotalPages(int size) {
        long totalRoutes = getTotalRoutes();
        return (int) Math.ceil((double) totalRoutes / size);
    }

    @Override
    public List<RouteVariant> getRouteVariantsByRouteId(Long routeId) {
        return routeRepository.findRouteVariantsByRouteId(routeId);
    }

    @Override
    public Route getRouteById(Long id) {
        return routeRepository.findById(id);
    }

    @Override
    public void saveRoute(Route route) {
        routeRepository.save(route);
    }

    @Override
    public void updateRoute(Route route) {
        routeRepository.update(route);
    }

    @Override
    public void deleteRoute(Long id) {
        routeRepository.delete(id);
    }

    @Override
    public List<Route> searchRoutes(Map<String, String> params, int page, int size) {
        return routeRepository.searchRoutes(params, page, size);
    }

    @Override
    public long countRoutesByParams(Map<String, String> params) {
        return routeRepository.countRoutesByParams(params);
    }

    @Override
    public int getTotalPagesByParams(Map<String, String> params, int size) {
        long totalRoutes = countRoutesByParams(params);
        return (int) Math.ceil((double) totalRoutes / size);
    }
}