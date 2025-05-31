package com.publictransport.controllers;


import com.publictransport.services.TrafficReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/manage-traffic-reports")
public class TrafficReportController {
    @Autowired
    private TrafficReportService trafficReportService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String manageReports(Model model) {
        model.addAttribute("reports", trafficReportService.getPendingReports());
        return "traffic/report-manage";
    }

    @GetMapping("/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String approveReport(@PathVariable("id") Long id, RedirectAttributes redirect) {
        try {
            trafficReportService.approveReport(id);
            redirect.addFlashAttribute("successMsg", "Báo cáo đã được duyệt.");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/manage-traffic-reports";
    }

    @GetMapping("/reject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String rejectReport(@PathVariable("id") Long id, RedirectAttributes redirect) {
        try {
            trafficReportService.rejectReport(id);
            redirect.addFlashAttribute("successMsg", "Báo cáo đã bị từ chối.");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/manage-traffic-reports";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteReport(@PathVariable("id") Long id, RedirectAttributes redirect) {
        try {
            trafficReportService.deleteReport(id);
            redirect.addFlashAttribute("successMsg", "Báo cáo đã được xóa.");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/manage-traffic-reports";
    }
}
