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
    public List<Route> findAllRoutes(int page, int size) {
        return routeRepository.findAllRoutes(page, size);
    }

    @Override
    public long countAllRoutes() {
        return routeRepository.countAllRoutes();
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
    public List<RouteVariant> findRouteVariantsByRouteId(Long routeId) {
        return routeRepository.findRouteVariantsByRouteId(routeId);
    }

    @Override
    public Route findById(Long id) {
        return routeRepository.findById(id);
    }

    @Override
    public void save(Route route) {
        routeRepository.save(route);
    }

    @Override
    public void update(Route route) {
        routeRepository.update(route);
    }

    @Override
    public void delete(Long id) {
        routeRepository.delete(id);
    }
}