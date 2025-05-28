package com.publictransport.services;

import com.publictransport.dto.params.RouteNavigationFilter;
import com.publictransport.dto.routenavigation.Hop;
import com.publictransport.dto.routenavigation.RouteNavigation;

import java.util.List;
import java.util.Optional;

public interface RouteNavigationService {
    // tìm đường đi trực tiếp giữa hai điểm
    public List<RouteNavigation> findDirectRouteNavigations(RouteNavigationFilter filter);
    public Optional<RouteNavigationFilter> buildNewFilterByKeyword(RouteNavigationFilter oldFilter);
}
