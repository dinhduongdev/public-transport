package com.publictransport.repositories;

import com.publictransport.models.Stop;

import java.util.List;
import java.util.Optional;

public interface StopRepository {
    List<Stop> findStopsByRouteVariantId(Long routeVariantId);

    void save(Stop stop);

    Optional<Stop> findById(Long id);

    void deleteByRouteVariantId(Long routeVariantId);
    
}