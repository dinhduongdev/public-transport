package com.publictransport.controllers;

import com.publictransport.dto.params.RouteNavigationFilter;
import com.publictransport.dto.routenavigation.RouteNavigation;
import com.publictransport.services.RouteNavigationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/navigation")
@CrossOrigin
public class APIRouteNavigationController {
    private final RouteNavigationService routeNavigationService;

    @Autowired
    public APIRouteNavigationController(RouteNavigationService routeNavigationService) {
        this.routeNavigationService = routeNavigationService;
    }

    @GetMapping
    public ResponseEntity<List<RouteNavigation>> routeNavigation(@Valid RouteNavigationFilter params) {
        List<RouteNavigation> res;
        if(params.getMaxNumOfTrip() == 1){
            res = routeNavigationService.findDirectRouteNavigations(params);
        }
        else {
            res = routeNavigationService.findRouteNavigations(params);
        }
        return ResponseEntity.ok(res);
    }

}
