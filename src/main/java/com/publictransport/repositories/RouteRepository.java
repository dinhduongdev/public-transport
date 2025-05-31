package com.publictransport.repositories;

import com.publictransport.dto.params.RouteFilter;
import com.publictransport.models.Route;
import jakarta.data.repository.Param;
import jakarta.data.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RouteRepository {
    Optional<Route> findById(Long id);

    Optional<Route> findByIdWithRouteVariants(@Param("id") Long id);

    Optional<Route> findById(Long id, boolean fetchRouteVariants);

    void save(Route route);

    void update(Route route);

    void delete(Long id);

    List<Route> findRoutes(RouteFilter optionalRouteFilter);

    List<Route> findRoutes(RouteFilter optionalRouteFilter, boolean fetchRouteVariants);

    List<Route> getAllRoutes();

    Long countRoutes(RouteFilter optionalRouteFilter);
}