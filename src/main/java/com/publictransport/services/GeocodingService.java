package com.publictransport.services;

import com.publictransport.dto.GeocodingResponse.GeocodingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class GeocodingService {
    @Value("${map.restapi.key}")
    private String apiKey;

    @Value("${goong.geocoding.url:https://rsapi.goong.io/Geocode}")
    private String geocodingUrl;

    public GeocodingResponse reverseGeocode(double latitude, double longitude) {
        String url = geocodingUrl + "?latlng=" + latitude + "," + longitude + "&api_key=" + apiKey;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, GeocodingResponse.class);
    }
}