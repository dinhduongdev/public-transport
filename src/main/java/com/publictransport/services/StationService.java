/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.services;

import com.publictransport.models.Station;
import java.util.List;

/**
 *
 * @author duong
 */
public interface StationService {
    List<Station> getStations();
    Station getStationById(Long id);
}
