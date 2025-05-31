package com.publictransport.dto;

import com.publictransport.models.ApprovalStatus;
import com.publictransport.models.TrafficStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class TrafficReportDTO {
    private Long userId;
    private String location;
    private Double latitude;
    private Double longitude;
    private String description;
    private TrafficStatus status;
    private ApprovalStatus approvalStatus;
    private LocalDateTime createdAt;
    private MultipartFile imageUrl;
}
