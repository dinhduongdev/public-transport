package com.publictransport.services;

import com.publictransport.models.Stop;

import java.util.List;
import java.util.Optional;

public interface StopService {
    List<Stop> findStopsByRouteVariantId(Long routeVariantId);

    void save(Stop stop);

    Optional<Stop> findById(Long id);

    void deleteByRouteVariantId(Long routeVariantId);

    List<Stop> findNearbyStops(String coordsString, double radiusKm, int limit);
}
