package com.publictransport.services;

import com.publictransport.models.RouteVariant;

import java.util.List;
import java.util.Map;

public interface RouteVariantService {
    List<RouteVariant> findAllRouteVariants(int page, int size);
    long countAllRouteVariants();
    List<RouteVariant> searchRouteVariants(Map<String, String> params, int page, int size);
    long countRouteVariantsByParams(Map<String, String> params);
    RouteVariant findById(Long id);
    void save(RouteVariant routeVariant);
    void update(RouteVariant routeVariant);
    void delete(Long id);
    List<RouteVariant> findByRouteId(Long routeId);
}
