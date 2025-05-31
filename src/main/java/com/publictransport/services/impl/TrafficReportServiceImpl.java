package com.publictransport.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.publictransport.dto.TrafficReportDTO;
import com.publictransport.dto.TrafficReportResponseDTO;
import com.publictransport.dto.UserDTO;
import com.publictransport.models.*;
import com.publictransport.proxies.MediaFileProxy;
import com.publictransport.repositories.NotificationRepository;
import com.publictransport.repositories.TrafficReportRepository;
import com.publictransport.repositories.UserRepository;
import com.publictransport.services.TrafficReportService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class TrafficReportServiceImpl  implements TrafficReportService {
    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private TrafficReportRepository trafficReportRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private MediaFileProxy cloudinaryProxy;


    @Override
    public TrafficReport createReport(TrafficReportDTO trafficReportDTO) throws IOException {
        User user = userRepository.findById(trafficReportDTO.getUserId());

        TrafficReport report = new TrafficReport();
        report.setUser(user);
        report.setLocation(trafficReportDTO.getLocation());
        report.setLatitude(trafficReportDTO.getLatitude());
        report.setLongitude(trafficReportDTO.getLongitude());
        report.setDescription(trafficReportDTO.getDescription());
        report.setStatus(trafficReportDTO.getStatus());
        report.setApprovalStatus(trafficReportDTO.getApprovalStatus() != null
                ? trafficReportDTO.getApprovalStatus()
                : ApprovalStatus.PENDING);
        MultipartFile image = trafficReportDTO.getImageUrl();
        if (image != null && !image.isEmpty()) {
            Map uploadResult = (Map) this.cloudinaryProxy.uploadFile(
                    image.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));
            report.setImageUrl(uploadResult.get("secure_url").toString());
        }
        return this.trafficReportRepository.save(report);
    }

    @Override
    public List<TrafficReport> getApprovedReports() {
        return trafficReportRepository.findByApprovalStatus(ApprovalStatus.APPROVED);
    }

    @Override
    public List<TrafficReport> getPendingReports() {
        return trafficReportRepository.findByApprovalStatus(ApprovalStatus.PENDING);
    }

    @Override
    public List<TrafficReportResponseDTO> getPendingReportsDTO() {
        List<TrafficReport> reports = trafficReportRepository.findByApprovalStatus(ApprovalStatus.PENDING);
        return reports.stream().map(report -> {
            TrafficReportResponseDTO dto = new TrafficReportResponseDTO();
            dto.setId(report.getId());
            dto.setUser(new UserDTO(report.getUser()));
            dto.setLocation(report.getLocation());
            dto.setLatitude(report.getLatitude());
            dto.setLongitude(report.getLongitude());
            dto.setDescription(report.getDescription());
            dto.setStatus(report.getStatus().name());
            dto.setApprovalStatus(report.getApprovalStatus());
            dto.setImageUrl(report.getImageUrl());
            dto.setCreatedAt(report.getCreatedAt());
            return dto;
        }).toList();
    }

    @Override
    public List<TrafficReportResponseDTO> getApprovedReportsDTO() {
        List<TrafficReport> reports = trafficReportRepository.findByApprovalStatus(ApprovalStatus.APPROVED);
        return reports.stream().map(report -> {
            TrafficReportResponseDTO dto = new TrafficReportResponseDTO();
            dto.setId(report.getId());
            dto.setUser(new UserDTO(report.getUser()));
            dto.setLocation(report.getLocation());
            dto.setLatitude(report.getLatitude());
            dto.setLongitude(report.getLongitude());
            dto.setDescription(report.getDescription());
            dto.setStatus(report.getStatus().name());
            dto.setApprovalStatus(report.getApprovalStatus());
            dto.setImageUrl(report.getImageUrl());
            dto.setCreatedAt(report.getCreatedAt());
            return dto;
        }).toList();
    }

    @Override
    public void approveReport(Long reportId) {
        TrafficReport report = trafficReportRepository.findById(reportId);
        if (report == null) {
            throw new RuntimeException("Không tìm thấy báo cáo");
        }
        report.setApprovalStatus(ApprovalStatus.APPROVED);
        trafficReportRepository.save(report);

        // Gửi thông báo
        if (report.getUser() != null) {
            Notification notification = new Notification();
            notification.setUser(report.getUser());
            notification.setTitle("Báo cáo kẹt xe được duyệt");
            notification.setMessage("Báo cáo kẹt xe tại " + report.getLocation() + " đã được duyệt.");
            notification.setCreatedAt(LocalDateTime.now());
            notification.setIsRead(false);
            notificationRepository.save(notification);
        }
    }

    @Override
    public void rejectReport(Long reportId) {
        TrafficReport report = trafficReportRepository.findById(reportId);
        if (report == null) {
            throw new RuntimeException("Không tìm thấy báo cáo");
        }
        report.setApprovalStatus(ApprovalStatus.REJECTED);
        trafficReportRepository.save(report);

        // Gửi thông báo
        if (report.getUser() != null) {
            Notification notification = new Notification();
            notification.setUser(report.getUser());
            notification.setTitle("Báo cáo kẹt xe bị từ chối");
            notification.setMessage("Báo cáo kẹt xe tại " + report.getLocation() + " đã bị từ chối.");
            notification.setCreatedAt(LocalDateTime.now());
            notification.setIsRead(false);
            notificationRepository.save(notification);
        }
    }

    @Override
    public void deleteReport(Long reportId) {
        TrafficReport report = trafficReportRepository.findById(reportId);
        if (report == null) {
            throw new RuntimeException("Không tìm thấy báo cáo");
        }
        // Xóa hình ảnh trên Cloudinary
//        if (report.getImageUrl() != null) {
//            try {
//                String publicId = extractPublicId(report.getImageUrl());
//                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
//            } catch (Exception e) {
//                System.err.println("Lỗi khi xóa hình ảnh: " + e.getMessage());
//            }
//        }
        trafficReportRepository.delete(report);
    }
    private String extractPublicId(String imageUrl) {
        String[] parts = imageUrl.split("/");
        String fileName = parts[parts.length - 1];
        return "traffic_reports/" + fileName.substring(0, fileName.lastIndexOf("."));
    }
}
