package com.publictransport.repositories;

import com.publictransport.models.RouteVariant;

import java.util.List;

public interface RouteVariantRepository {
    List<RouteVariant> findAllRouteVariants(int page, int size);
    Long countRouteVariants();

}
