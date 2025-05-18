package com.publictransport.repositories;

import com.publictransport.models.Stop;

import java.util.List;

public interface StopRepository {
    List<Stop> findStopsByRouteVariantId(Long routeVariantId);
    void save(Stop stop);
    Stop findById(Long id);
    void deleteByRouteVariantId(Long routeVariantId);
}