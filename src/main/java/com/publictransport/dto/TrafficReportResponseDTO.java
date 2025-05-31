package com.publictransport.dto;

import com.publictransport.models.ApprovalStatus;
import com.publictransport.models.TrafficReport;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrafficReportResponseDTO {
    private Long id;
    private UserDTO user;
    private String location;
    private Double latitude;
    private Double longitude;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private ApprovalStatus approvalStatus;
    private String imageUrl;
    private LocalDateTime createdAt;
}
