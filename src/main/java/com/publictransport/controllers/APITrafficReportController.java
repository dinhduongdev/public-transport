package com.publictransport.controllers;


import com.publictransport.dto.TrafficReportDTO;
import com.publictransport.dto.TrafficReportResponseDTO;
import com.publictransport.models.TrafficReport;
import com.publictransport.models.User;
import com.publictransport.services.TrafficReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/traffic-reports")
public class APITrafficReportController {
    @Autowired
    private TrafficReportService trafficReportService;


    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> submitReport(@ModelAttribute TrafficReportDTO trafficReportDTO) throws IOException {
        return new ResponseEntity<>(this.trafficReportService.createReport(trafficReportDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TrafficReportResponseDTO>> getApprovedReports() {
        return ResponseEntity.ok(trafficReportService.getApprovedReportsDTO());
    }
}
