package com.publictransport.dto.routenavigation;

import com.publictransport.models.Coordinates;
import lombok.Data;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
public class RouteNavigation {
    // hai trường này để xác định điểm bắt đầu và kết thúc của hành trình
    private Coordinates startCoordinates;
    private Coordinates endCoordinates;
    private double totalDistanceInMeters;
    private Duration duration;
    private List<Hop> hops;

    public RouteNavigation() {
        hops = new ArrayList<>();
        duration = Duration.ZERO;
    }

    public void addHop(Hop hop) {
        hops.add(hop);
    }

    public double calculateTotalDistance() {
        return hops.stream()
                .mapToDouble(Hop::getDistanceToNextStop)
                .sum();
    }
}
