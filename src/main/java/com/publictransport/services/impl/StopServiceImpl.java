package com.publictransport.services.impl;

import com.publictransport.models.Stop;
import com.publictransport.repositories.StopRepository;
import com.publictransport.services.StopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StopServiceImpl implements StopService {
    @Autowired
    private StopRepository stopRepository;

    @Override
    public List<Stop> findStopsByRouteVariantId(Long routeVariantId) {
        return stopRepository.findStopsByRouteVariantId(routeVariantId);
    }
    @Override
    public void save(Stop stop) {
        stopRepository.save(stop);
    }

    @Override
    public Optional<Stop> findById(Long id) {
        return stopRepository.findById(id);
    }

    @Override
    public void deleteByRouteVariantId(Long routeVariantId) {
        stopRepository.deleteByRouteVariantId(routeVariantId);
    }
}
