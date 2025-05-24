package com.publictransport.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.publictransport.dto.DirectionRequest;
import com.publictransport.models.Coordinates;
import com.publictransport.models.Stop;
import com.publictransport.proxies.MapProxy;
import com.publictransport.repositories.StopRepository;
import com.publictransport.services.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class GoongMapService implements MapService {

    private final MapProxy mapProxy;
    private final StopRepository stopRepository;

    @Autowired
    public GoongMapService(MapProxy mapProxy, StopRepository stopRepository) {
        this.mapProxy = mapProxy;
        this.stopRepository = stopRepository;
    }

    public JsonNode getDirection(Coordinates origin, List<Coordinates> destinations) {
        DirectionRequest directionRequest = new DirectionRequest();
        String originStr = origin.getLat() + "," + origin.getLng();
        String destinationStr = destinations
                .stream()
                .map(destination ->
                        destination.getLat() + "," + destination.getLng())
                .collect(Collectors.joining(";"));
        directionRequest.setOrigin(originStr);
        directionRequest.setDestination(destinationStr);
        return mapProxy.getDirections(directionRequest);
    }

    @Override
    public JsonNode getDirectionByRouteVarId(Long routeVarId) {
        List<Stop> stops = stopRepository.findStopsByRouteVariantId(routeVarId);
        if (stops.isEmpty())
            throw new NoSuchElementException("Route variant has no stops");

        Coordinates origin = stops.get(0).getStation().getCoordinates();
        List<Coordinates> destinations = stops.stream()
                .skip(1)
                .map(stop -> stop.getStation().getCoordinates())
                .collect(Collectors.toList());
        return getDirection(origin, destinations);
    }
}
