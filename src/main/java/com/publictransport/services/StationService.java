package com.publictransport.services;

import com.publictransport.dto.params.StationFilter;
import com.publictransport.models.Coordinates;
import com.publictransport.models.Station;

import java.util.List;
import java.util.Optional;

public interface StationService {
    Optional<Station> findById(Long id);

    void save(Station station);

    void update(Station station);

    void delete(Long id);

    List<Station> findStations(StationFilter filter);

    List<Station> findStations(StationFilter filter, boolean fetchStops);

    List<Station> getAllStations();

    // trả về filter mới với địa chỉ đã được proxy tìm kiếm
    Optional<StationFilter> buildNewFilterByKeyword(StationFilter oldFilter);

    Optional<StationFilter> buildNewFilterByKeyword(String keyword);

    long countStations(StationFilter filter);
}
