/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.repositories;

import java.util.List;

/**
 *
 * @author duong
 */
public interface StationRepository {
    List<Station> getStations();
    Station getStationById(Long id);
}
