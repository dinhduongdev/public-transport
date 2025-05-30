package com.publictransport.proxies.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.publictransport.dto.DirectionRequest;
import com.publictransport.models.Coordinates;
import com.publictransport.proxies.MapProxy;
import com.publictransport.utils.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Component
public class GoongMapProxy implements MapProxy {

    private final String REST_API_KEY;
    private final String REST_API_BASE_URL;
    private final String DIRECTIONS_ENDPOINT;
    private final String GEOCODE_ENDPOINT;
    private final RestTemplate restTemplate;

    @Autowired
    public GoongMapProxy(
            @Value("${map.restapi.key}") String restApiKey,
            @Value("${map.restapi.base_url}") String restApiBaseUrl,
            @Value("${map.restapi.directions_endpoint}") String directionsEndpoint,
            @Value("${map.restapi.geocode_endpoint}") String geocodeEndpoint,
            RestTemplate restTemplate
    ) {
        this.REST_API_KEY = restApiKey;
        this.REST_API_BASE_URL = restApiBaseUrl;
        this.DIRECTIONS_ENDPOINT = directionsEndpoint;
        this.restTemplate = restTemplate;
        this.GEOCODE_ENDPOINT = geocodeEndpoint;
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

    @Override
    public Optional<Pair<String, Coordinates>> getCoordinates(String kw) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(REST_API_BASE_URL)
                .path(GEOCODE_ENDPOINT)
                .queryParam("address", kw)
                .queryParam("api_key", REST_API_KEY)
                .build()
                .toUri();
        // Log the URI for debugging purposes
        System.out.println("Requesting coordinates from URI: " + uri);
        ResponseEntity<String> response;

        try{
            response = restTemplate.getForEntity(uri, String.class);
        } catch (RestClientException e) {
            System.err.println("Exception while fetching coordinates: " + kw + " - " + e.getMessage());
            return Optional.empty();
        }


        ObjectMapper mapper = new ObjectMapper();
        JsonNode firstResult;
        try {
            firstResult = mapper.readTree(response.getBody()).get("results").get(0);
            String formattedAddress = firstResult.get("formatted_address").asText();
            JsonNode coordinates = firstResult.get("geometry").get("location");
            System.out.println("Coordinates for " + formattedAddress + ": " + coordinates.get("lat") + ", " + coordinates.get("lng"));
            Coordinates coords = new Coordinates(
                    coordinates.get("lat").doubleValue(),
                    coordinates.get("lng").doubleValue()
            );
            return Optional.of(new ImmutablePair<>(formattedAddress, coords));
        } catch (Exception e) {
            System.err.println("Error parsing response for coordinates: " + kw);
            return Optional.empty();
        }
    }
}
