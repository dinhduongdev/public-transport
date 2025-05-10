/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.repositories;

import java.util.List;

/**
 *
 * @author duong
 */
public interface StopRepository {
    List<Stop> getStops();
    List<Stop> getStopsByRouteId(Long id);
    Stop saveStop(Stop stop);
    void deleteStopsByRouteId(Long routeId);
}
