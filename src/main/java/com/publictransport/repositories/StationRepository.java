package com.publictransport.repositories;

import com.publictransport.dto.params.StationFilter;
import com.publictransport.models.Station;

import java.util.List;
import java.util.Optional;

public interface StationRepository {
    Optional<Station> findById(Long id);

    void save(Station station);

    Optional<Station> findDuplicate(Station station);

    void update(Station station);

    void delete(Long id);

    List<Station> findStations(StationFilter filter);

    List<Station> findStations(StationFilter filter, boolean fetchStops);

    List<Station> getAllStations();

    long countStations(StationFilter filter);

    List<Long> getStationIds(StationFilter filter);

    List<Station> getStationsByIds(List<Long> ids, boolean fetchStops);

}
