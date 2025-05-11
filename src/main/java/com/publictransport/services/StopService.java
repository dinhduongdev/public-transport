package com.publictransport.services;

import com.publictransport.models.Stop;

import java.util.List;

public interface StopService {
    List<Stop> findStopsByRouteVariantId(Long routeVariantId);
    void save(Stop stop);
}
