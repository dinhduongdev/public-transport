/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.controllers;

import com.publictransport.dto.RouteDTO;
import com.publictransport.models.Route;
import com.publictransport.models.Station;
import com.publictransport.models.Stop;
import com.publictransport.services.RouteService;
import com.publictransport.services.StationService;
import com.publictransport.services.StopService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author duong
 */
@Controller
@Transactional
public class RouteController {

    @Autowired
    RouteService routeService;
    @Autowired
    StopService stopService;
    @Autowired
    StationService stationService;

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/manage-routes")
    public String viewRoute(Model model) {
        List<Route> routes = this.routeService.getRoutes();
        List<RouteDTO> routeDTOs = new ArrayList<>();
        for (Route route : routes) {
            System.out.println(route.getId());


            Stop[] firstAndLastStop = this.stopService.getFirstAndLastStop(route.getId());
            Station firstStation = firstAndLastStop[0].getStationId();
            Station lastStation = firstAndLastStop[1].getStationId();
            System.out.println(firstAndLastStop[0].getId());
            System.out.println(firstAndLastStop[1].getId());
            System.out.println("bat dau" + firstStation);
            System.out.println("ket thuc" + lastStation);


            RouteDTO routeDTO = new RouteDTO(route, firstStation.getStopName(), lastStation.getStopName());

            routeDTOs.add(routeDTO);
        }
        List<Station> stationsList = this.stationService.getStations();
        model.addAttribute("routeDTOs", routeDTOs);
        model.addAttribute("stationsList", stationsList);
        return "route";
    }

    @PostMapping("/manage-routes")
    public String addRoute( Model model,
                            @RequestParam("routeName") String routeName,
                           @RequestParam("stops") List<Long> stationIds
                           ) {
        //Create a new Route
        Route route = new Route();
        route.setName(routeName);
        route = this.routeService.saveRoute(route);
        // Create Stop entries for each selected station
        int stopOrder = 1;
        for (Long stationId : stationIds) {
            Station station =  this.stationService.getStationById(stationId);
            Stop stop = new Stop();
            stop.setRouteId(route);
            stop.setStationId(station);
            stop.setStopOrder(stopOrder++);
            stopService.saveStop(stop);
        }
        return "redirect:/manage-routes";
    }
    @GetMapping("/manage-routes/detail/{id}")
    public String viewDetailRoute(@PathVariable("id") Long id, Model model) {
        return "route-detail";
    }


    @GetMapping("/manage-routes/edit/{id}")
    public String editRouteForm(@PathVariable("id") Long id, Model model) {
        Route route = routeService.getRouteById(id);
        List<Stop> stops = stopService.getStopsByRouteId(id);
        List<Long> selectedStationIds = stops.stream()
                .map(stop -> stop.getStationId().getId())
                .collect(Collectors.toList());
        List<Station> stationsList = stationService.getStations();
        model.addAttribute("route", route);
        model.addAttribute("selectedStationIds", selectedStationIds);
        model.addAttribute("stationsList", stationsList);
        return "edit-route";
    }
    @PostMapping("/manage-routes/edit/{id}")
    public String updateRoute(@PathVariable("id") Long id,
                              @RequestParam("routeName") String routeName,
                              @RequestParam("stops") List<Long> stationIds) {
        Route route = routeService.getRouteById(id);
        route.setName(routeName);
        routeService.saveRoute(route);
        stopService.deleteStopsByRouteId(id);
        int stopOrder = 1;
        for (Long stationId : stationIds) {
            Station station = stationService.getStationById(stationId);
            Stop stop = new Stop();
            stop.setRouteId(route);
            stop.setStationId(station);
            stop.setStopOrder(stopOrder++);
            stopService.saveStop(stop);
        }
        return "redirect:/manage-routes";
    }
    @GetMapping("/manage-routes/delete/{id}")
    public String deleteRoute(@PathVariable("id") Long id) {
        stopService.deleteStopsByRouteId(id);
        routeService.deleteRoute(id);
        return "redirect:/manage-routes";
    }
}
