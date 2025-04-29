/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.services.impl;

import com.publictransport.models.Stop;
import com.publictransport.repositories.StopRepository;
import com.publictransport.services.StopService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author duong
 */
@Service
public class StopServiceImpl implements StopService {

    @Autowired
    private StopRepository stopRepository;

    @Override
    public List<Stop> getStops() {
        return this.stopRepository.getStops();
    }

    @Override
    public List<Stop> getStopsByRouteId(Long id) {
        return this.stopRepository.getStopsByRouteId(id);
    }


    @Override
    public Stop[] getFirstAndLastStop(Long routeId) {
        List<Stop> stops = this.stopRepository.getStopsByRouteId(routeId);
        System.out.println(stops);
        if (stops == null || stops.isEmpty()) {
            System.out.println("null");
            return new Stop[]{null, null};
        }
        
        Stop firstStop = stops.get(0);
        Stop lastStop = stops.get(stops.size() - 1);
        return new Stop[]{firstStop, lastStop};
    }

    @Override
    public Stop saveStop(Stop stop) {
        return this.stopRepository.saveStop(stop);
    }

    @Override
    public void deleteStopsByRouteId(Long routeId) {
        this.stopRepository.deleteStopsByRouteId(routeId);
    }

}
