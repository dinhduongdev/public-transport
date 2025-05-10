/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.dto;

import java.util.List;

/**
 *
 * @author duong
 */
public class RouteDTO {
    private Route route;
    private String firstStationName;
    private String lastStationName;
    private List<Station> stationsList;

    // Constructor với 4 tham số
    public RouteDTO(Route route, String firstStationName, String lastStationName, List<Station> stationsList) {
        this.route = route;
        this.firstStationName = firstStationName;
        this.lastStationName = lastStationName;
        this.stationsList = stationsList;
    }

    // Constructor với 3 tham số
    public RouteDTO(Route route, String firstStationName, String lastStationName) {
        this.route = route;
        this.firstStationName = firstStationName;
        this.lastStationName = lastStationName;
    }

    // Getter và Setter cho route
    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    // Getter và Setter cho firstStationName
    public String getFirstStationName() {
        return firstStationName;
    }

    public void setFirstStationName(String firstStationName) {
        this.firstStationName = firstStationName;
    }

    // Getter và Setter cho lastStationName
    public String getLastStationName() {
        return lastStationName;
    }

    public void setLastStationName(String lastStationName) {
        this.lastStationName = lastStationName;
    }

    // Getter và Setter cho stationsList
    public List<Station> getStationsList() {
        return stationsList;
    }

    public void setStationsList(List<Station> stationsList) {
        this.stationsList = stationsList;
    }
}