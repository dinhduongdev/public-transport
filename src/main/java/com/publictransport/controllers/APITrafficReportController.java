package com.publictransport.controllers;


import com.publictransport.dto.TrafficReportDTO;
import com.publictransport.dto.TrafficReportResponseDTO;
import com.publictransport.models.TrafficReport;
import com.publictransport.models.User;
import com.publictransport.repositories.UserRepository;
import com.publictransport.services.TrafficReportService;
import com.publictransport.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/traffic-reports")
public class APITrafficReportController {
    @Autowired
    private TrafficReportService trafficReportService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> submitReport(
            @ModelAttribute TrafficReportDTO trafficReportDTO,
            HttpServletRequest request
            ) throws IOException {
        Long userIdFromToken = getUserIdFromRequest(request);
        if (userIdFromToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập để thực hiện hành động này.");
        }
        // So sánh userId từ FE và userId token
        if (!userIdFromToken.equals(trafficReportDTO.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền gửi báo cáo thay người khác.");
        }

        return new ResponseEntity<>(this.trafficReportService.createReport(trafficReportDTO), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TrafficReportResponseDTO>> getApprovedReports() {
        return ResponseEntity.ok(trafficReportService.getApprovedReportsDTO());
    }




    private Long getUserIdFromRequest(HttpServletRequest request) {
        try {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                String email = jwtUtils.validateTokenAndGetEmail(token);

                if (email != null) {
                    Optional<User> userOpt = userRepository.getUserByEmail(email);
                    if (userOpt.isPresent()) {
                        return userOpt.get().getId();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
