package com.publictransport.services;

import com.publictransport.models.Station;

import java.util.List;
import java.util.Map;

public interface StationService {
    List<Station> findAllStations(int page, int size);
    long countAllStations();
    List<Station> searchStations(Map<String, String> params, int page, int size);
    long countStationsByParams(Map<String, String> params);
    Station findById(Long id);
    void save(Station station);
    void update(Station station);
    void delete(Long id);
}
