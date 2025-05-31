package com.publictransport.controllers;


import com.publictransport.dto.GeocodingResponse.GeocodingResponse;
import com.publictransport.services.GeocodingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geocode")
public class APIGeocodeController {
    @Autowired
    private GeocodingService geocodingService;

    @GetMapping("/reverse")
    public GeocodingResponse reverseGeocode(
            @RequestParam(value = "latitude") double latitude,
            @RequestParam(value = "longitude") double longitude) {
        return geocodingService.reverseGeocode(latitude, longitude);
    }
}
