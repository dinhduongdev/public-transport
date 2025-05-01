/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.services;

import com.publictransport.models.Stop;
import java.util.List;

/**
 *
 * @author duong
 */
public interface StopService {
    List<Stop> getStops();
    List<Stop> getStopsByRouteId(Long id);
    Stop[] getFirstAndLastStop(Long routeId);
    Stop saveStop(Stop stop);
    void deleteStopsByRouteId(Long routeId);
}
