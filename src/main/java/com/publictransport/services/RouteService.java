package com.publictransport.services;

import com.publictransport.models.Route;
import com.publictransport.models.RouteVariant;

import java.util.List;
import java.util.Map;

public interface RouteService {
    List<Route> findAllRoutes(int page, int size);
    long countAllRoutes();
    List<Route> searchRoutes(Map<String, String> params, int page, int size);
    long countRoutesByParams(Map<String, String> params);
    List<RouteVariant> findRouteVariantsByRouteId(Long routeId);
    Route findById(Long id);
    void save(Route route);
    void update(Route route);
    void delete(Long id);
}