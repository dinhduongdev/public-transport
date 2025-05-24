package com.publictransport.services;

import com.publictransport.models.RouteVariant;

public interface MapService {
    Object getDirectionByRouteVarId(Long routeVarId) throws IllegalArgumentException;
}
