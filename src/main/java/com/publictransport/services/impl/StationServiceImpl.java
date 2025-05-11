package com.publictransport.services.impl;

import com.publictransport.models.Station;
import com.publictransport.repositories.StationRepository;
import com.publictransport.services.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
@Transactional
public class StationServiceImpl implements StationService {
    @Autowired
    private StationRepository stationRepository;


    @Override
    public List<Station> findAllStations(int page, int size) {
        return stationRepository.findAllStations(page, size);
    }

    @Override
    public long countAllStations() {
        return stationRepository.countAllStations();
    }

    @Override
    public List<Station> searchStations(Map<String, String> params, int page, int size) {
        return stationRepository.searchStations(params, page, size);
    }

    @Override
    public long countStationsByParams(Map<String, String> params) {
        return stationRepository.countStationsByParams(params);
    }

    @Override
    public Station findById(Long id) {
        return stationRepository.findById(id);
    }

    @Override
    public void save(Station station) {
        stationRepository.save(station);
    }

    @Override
    public void update(Station station) {
        stationRepository.update(station);
    }

    @Override
    public void delete(Long id) {
        stationRepository.delete(id);
    }
}
