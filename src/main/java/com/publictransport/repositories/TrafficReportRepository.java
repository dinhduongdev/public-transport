package com.publictransport.repositories;



import com.publictransport.models.ApprovalStatus;
import com.publictransport.models.TrafficReport;

import java.util.List;

public interface TrafficReportRepository {
    TrafficReport save(TrafficReport report);
    TrafficReport findById(Long id);
    List<TrafficReport> findByApprovalStatus(ApprovalStatus approvalStatus);
    List<TrafficReport> findByLocationContaining(String location);
    void delete(TrafficReport report);
}
