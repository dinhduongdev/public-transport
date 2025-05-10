package com.publictransport.services;

import com.publictransport.models.RouteVariant;

import java.util.List;

public interface RouteVariantService {
    List<RouteVariant> getRouteVariants(int page, int size);
    Long countRouteVariants();
}
