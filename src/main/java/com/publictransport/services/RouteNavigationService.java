package com.publictransport.services;

import com.publictransport.dto.params.RouteNavigationFilter;
import com.publictransport.dto.routenavigation.RouteNavigation;

import java.util.List;
import java.util.Optional;

public interface RouteNavigationService {
    // tìm đường đi trực tiếp giữa hai điểm
    List<RouteNavigation> findDirectRouteNavigations(RouteNavigationFilter filter);

    List<RouteNavigation> findRouteNavigations(RouteNavigationFilter filter);
}
