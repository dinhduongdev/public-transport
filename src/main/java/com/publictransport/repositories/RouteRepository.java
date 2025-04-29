/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.repositories;

import com.publictransport.models.Route;
import java.util.List;

/**
 *
 * @author duong
 */
public interface RouteRepository {
    List<Route> getRoutes();
    Route saveRoute(Route route);
    Route getRouteById(Long id);
    void deleteRoute(Long id);
}
