package com.publictransport.proxies.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.publictransport.dto.DirectionRequest;
import com.publictransport.proxies.MapProxy;
import jakarta.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class GoongMapProxy implements MapProxy {

    private final String REST_API_KEY;
    private final String REST_API_BASE_URL;
    private final String DIRECTIONS_ENDPOINT;
    private final RestTemplate restTemplate;

    @Autowired
    public GoongMapProxy(
            @Value("${map.restapi.key}") String restApiKey,
            @Value("${map.restapi.base_url}") String restApiBaseUrl,
            @Value("${map.restapi.directions_endpoint}") String directionsEndpoint,
            RestTemplate restTemplate
    ) {
        this.REST_API_KEY = restApiKey;
        this.REST_API_BASE_URL = restApiBaseUrl;
        this.DIRECTIONS_ENDPOINT = directionsEndpoint;
        this.restTemplate = restTemplate;
    }

    @Override
    public JsonNode getDirections(DirectionRequest directionRequest) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(REST_API_BASE_URL)
                .path(DIRECTIONS_ENDPOINT)
                .queryParam("origin", directionRequest.getOrigin())
                .queryParam("destination", directionRequest.getDestination())
                .queryParam("vehicle", directionRequest.getVehicle())
                .queryParam("api_key", REST_API_KEY)
                .build()
                .toUri();
        // Log the URI for debugging purposes
        System.out.println("Requesting directions from URI: " + uri);
        return restTemplate.getForObject(uri, JsonNode.class);
    }
}
