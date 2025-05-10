/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.services;

import java.util.List;

/**
 *
 * @author duong
 */
public interface RouteService {
    List<Route> getRoutes();
    Route saveRoute(Route route);
    Route getRouteById(Long id);
    void deleteRoute(Long id);
}
