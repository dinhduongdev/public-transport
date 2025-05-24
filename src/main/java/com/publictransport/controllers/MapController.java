package com.publictransport.controllers;

import com.publictransport.services.MapService;
import com.publictransport.services.RouteVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/map")
@CrossOrigin
public class MapController {
    private final MapService mapService;

    @Autowired
    public MapController(MapService mapService, RouteVariantService routeVariantService) {
        this.mapService = mapService;
    }

    @GetMapping("/direction/{routeVarId:\\d+}")
    public ResponseEntity<Object> direction(@PathVariable("routeVarId") Long routeVarId) {
        Object direction = mapService.getDirectionByRouteVarId(routeVarId);
        return ResponseEntity.ok().body(direction);
    }
}
