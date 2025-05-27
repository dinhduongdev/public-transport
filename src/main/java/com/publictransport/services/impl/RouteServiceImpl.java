package com.publictransport.services.impl;

import com.publictransport.dto.params.RouteFilter;
import com.publictransport.models.Route;
import com.publictransport.proxies.MapProxy;
import com.publictransport.repositories.RouteRepository;
import com.publictransport.services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteServiceImpl implements RouteService {
    private final RouteRepository routeRepository;

    @Autowired
    public RouteServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public Optional<Route> findById(Long id) {
        return routeRepository.findById(id);
    }

    @Override
    public Optional<Route> findById(Long id, boolean fetchRouteVariants) {
        return routeRepository.findById(id, fetchRouteVariants);
    }

    @Override
    public void save(Route route) {
        routeRepository.save(route);
    }

    @Override
    public void update(Route route) {
        routeRepository.update(route);
    }

    @Override
    public void delete(Long id) {
        routeRepository.delete(id);
    }

    @Override
    public List<Route> findRoutes(RouteFilter filter) {
        return routeRepository.findRoutes(filter);
    }

    @Override
    public List<Route> findRoutes(RouteFilter filter, boolean fetchRouteVariants) {
        return routeRepository.findRoutes(filter, fetchRouteVariants);
    }

    @Override
    public List<Route> getAllRoutes() {
        return routeRepository.getAllRoutes();
    }

    @Override
    public Long countRoutes(RouteFilter filter) {
        return routeRepository.countRoutes(filter);
    }
}