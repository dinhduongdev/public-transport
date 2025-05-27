package com.publictransport.services.impl;

import com.publictransport.dto.params.StationFilter;
import com.publictransport.models.Station;
import com.publictransport.proxies.MapProxy;
import com.publictransport.repositories.StationRepository;
import com.publictransport.services.StationService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Transactional
public class StationServiceImpl implements StationService {
    private final StationRepository stationRepository;
    private final MapProxy mapProxy;

    @Autowired
    public StationServiceImpl(StationRepository stationRepository, MapProxy mapProxy) {
        this.stationRepository = stationRepository;
        this.mapProxy = mapProxy;
    }

    @Override
    public Optional<Station> findById(Long id) {
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

    @Override
    public List<Station> findStations(StationFilter filter) {
        return stationRepository.findStations(filter);
    }

    @Override
    public Optional<StationFilter> buildNewFilterByKeyword(StationFilter oldFilter) {
        var newFilter = new StationFilter();
        newFilter.setRadiusKm(oldFilter.getRadiusKm());
        newFilter.setPage(oldFilter.getPage());
        newFilter.setSize(oldFilter.getSize());

        var optAddrAndCoords = mapProxy.getCoordinates(oldFilter.getKeyword());

        if (optAddrAndCoords.isEmpty()) {
            return Optional.empty();
        }
        var addrAndCoords = optAddrAndCoords.get();
        newFilter.setFormattedAddress(addrAndCoords.getLeft());
        newFilter.setLng(addrAndCoords.getRight().getLng());
        newFilter.setLat(addrAndCoords.getRight().getLat());

        return Optional.of(newFilter);
    }

    @Override
    public List<Station> getAllStations() {
        return stationRepository.getAllStations();
    }

    @Override
    public long countStations(StationFilter filter) {
        return stationRepository.countStations(filter);
    }
}
