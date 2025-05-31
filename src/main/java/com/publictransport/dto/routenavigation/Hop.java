package com.publictransport.dto.routenavigation;

import com.publictransport.models.Stop;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Hop {
    private int order;
    private RouteDTO route;
    private Stop stop;
    private double distanceToNextStop;
}
