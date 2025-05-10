/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.services.impl;

import com.publictransport.repositories.RouteRepository;
import com.publictransport.services.RouteService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author duong
 */
@Service
public class RouteServiceImpl implements RouteService{
    @Autowired
    private RouteRepository routeRepository;
    @Override
    public List<Route> getRoutes() {
        return this.routeRepository.getRoutes();
    }

    @Override
    public Route saveRoute(Route route) {
        return this.routeRepository.saveRoute(route);
    }

    @Override
    public Route getRouteById(Long id) {
        return this.routeRepository.getRouteById(id);
    }

    @Override
    public void deleteRoute(Long id) {
        this.routeRepository.deleteRoute(id);
    }

}
