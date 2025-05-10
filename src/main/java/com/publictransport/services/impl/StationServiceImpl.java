/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.services.impl;

import com.publictransport.repositories.StationRepository;
import com.publictransport.services.StationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author duong
 */
@Service
public class StationServiceImpl implements StationService {

    @Autowired
    private StationRepository stationRepository;

    public List<Station> getStations() {
        return this.stationRepository.getStations();
    }

    @Override
    public Station getStationById(Long id) {
        return this.stationRepository.getStationById(id);
    }

}
