package com.publictransport.services;

import com.publictransport.models.Route;
import com.publictransport.models.RouteVariant;

import java.util.List;
import java.util.Map;

public interface RouteService {
    List<Route> getAllRoutes(int page, int size);
    long getTotalRoutes();
    int getTotalPages(int size);
    List<RouteVariant> getRouteVariantsByRouteId(Long routeId);
    Route getRouteById(Long id);
    void saveRoute(Route route);
    void updateRoute(Route route);
    void deleteRoute(Long id);
    List<Route> searchRoutes(Map<String, String> params, int page, int size);
    long countRoutesByParams(Map<String, String> params);
    int getTotalPagesByParams(Map<String, String> params, int size);
}