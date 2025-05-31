package com.publictransport.dto.GeocodingResponse;

public class GeocodingResponse {
    private String status;
    private Result[] results;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Result[] getResults() {
        return results;
    }

    public void setResults(Result[] results) {
        this.results = results;
    }
}

