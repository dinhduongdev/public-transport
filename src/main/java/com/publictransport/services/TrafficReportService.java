package com.publictransport.services;

import com.publictransport.dto.TrafficReportDTO;
import com.publictransport.dto.TrafficReportResponseDTO;
import com.publictransport.models.TrafficReport;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TrafficReportService {
    TrafficReport createReport(TrafficReportDTO trafficReportDTO) throws IOException;
    List<TrafficReport> getApprovedReports();
    List<TrafficReportResponseDTO> getApprovedReportsDTO();
    List<TrafficReport> getPendingReports();
    List<TrafficReportResponseDTO> getPendingReportsDTO();
    void approveReport(Long reportId);
    void rejectReport(Long reportId);
    void deleteReport(Long reportId);

    List<TrafficReport> getReportsByStatus(String status);
}
