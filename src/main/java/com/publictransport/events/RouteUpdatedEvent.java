package com.publictransport.events;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;


public class RouteUpdatedEvent extends ApplicationEvent {
    private final Long routeId;

    public RouteUpdatedEvent(Object source, Long routeId) {
        super(source);
        this.routeId = routeId;
    }

    public Long getRouteId() {
        return routeId;
    }

}
