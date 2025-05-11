package com.publictransport.controllers;

import com.publictransport.models.Schedule;
import com.publictransport.models.ScheduleTrip;
import com.publictransport.services.ScheduleService;
import com.publictransport.services.ScheduleTripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleTripService scheduleTripService;

    @GetMapping("/manage-schedules")
    public String getAllSchedules(Model model,
                                  @RequestParam(name = "page", defaultValue = "1") int page,
                                  @RequestParam(name = "size", defaultValue = "10") int size,
                                  @RequestParam(name = "routeVariantId", required = false) String routeVariantId,
                                  @RequestParam(name = "startDate", required = false) String startDate,
                                  @RequestParam(name = "endDate", required = false) String endDate,
                                  @RequestParam(name = "priority", required = false) String priority) {
        List<Schedule> schedules;
        long totalItems;
        int totalPages;

        // Tạo params để truyền vào phương thức tìm kiếm
        Map<String, String> params = new HashMap<>();
        if (routeVariantId != null && !routeVariantId.trim().isEmpty()) {
            params.put("routeVariantId", routeVariantId);
        }
        if (startDate != null && !startDate.trim().isEmpty()) {
            params.put("startDate", startDate);
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            params.put("endDate", endDate);
        }
        if (priority != null && !priority.trim().isEmpty()) {
            params.put("priority", priority);
        }

        // Nếu có tham số tìm kiếm, gọi phương thức tìm kiếm
        if (!params.isEmpty()) {
            schedules = scheduleService.searchSchedules(params, page, size);
            totalItems = scheduleService.countSchedulesByParams(params);
            totalPages = (int) Math.ceil((double) totalItems / size);
        } else {
            // Nếu không có tham số, lấy toàn bộ danh sách
            schedules = scheduleService.findAllSchedules(page, size);
            totalItems = scheduleService.countAllSchedules();
            totalPages = (int) Math.ceil((double) totalItems / size);
        }

        // Tạo map để lưu trạm đi và trạm đến cho mỗi Schedule
        Map<Long, Map<String, String>> stationInfoMap = new HashMap<>();
        for (Schedule schedule : schedules) {
            Map<String, String> stationInfo = new HashMap<>();
            if (schedule.getRouteVariant() != null) {
                stationInfo.put("startStop", schedule.getRouteVariant().getStartStop() != null ? schedule.getRouteVariant().getStartStop() : "N/A");
                stationInfo.put("endStop", schedule.getRouteVariant().getEndStop() != null ? schedule.getRouteVariant().getEndStop() : "N/A");
            } else {
                stationInfo.put("startStop", "N/A");
                stationInfo.put("endStop", "N/A");
            }
            stationInfoMap.put(schedule.getId(), stationInfo);
        }

        model.addAttribute("schedules", schedules);
        model.addAttribute("stationInfoMap", stationInfoMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("size", size);
        model.addAttribute("routeVariantId", routeVariantId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("priority", priority);
        return "schedule/schedules";
    }

    @GetMapping("/manage-schedules/detail/{id}")
    public String viewScheduleDetail(@PathVariable("id") Long id,
                                     @RequestParam(name = "tripPage", defaultValue = "1") int tripPage,
                                     @RequestParam(name = "tripSize", defaultValue = "5") int tripSize,
                                     Model model) {
        Schedule schedule = scheduleService.findById(id);
        if (schedule == null) {
            model.addAttribute("msg", "Lịch trình không tồn tại.");
            return "redirect:/manage-schedules";
        }

        // Lấy danh sách ScheduleTrip liên quan đến Schedule với phân trang
        List<ScheduleTrip> scheduleTrips = scheduleTripService.findByScheduleId(id, tripPage, tripSize);
        long totalTrips = scheduleTripService.countByScheduleId(id);
        int totalTripPages = (int) Math.ceil((double) totalTrips / tripSize);

        model.addAttribute("schedule", schedule);
        model.addAttribute("scheduleTrips", scheduleTrips);
        model.addAttribute("tripPage", tripPage);
        model.addAttribute("tripSize", tripSize);
        model.addAttribute("totalTrips", totalTrips);
        model.addAttribute("totalTripPages", totalTripPages);
        return "schedule/schedule-detail";
    }

    @GetMapping("/manage-schedules/add")
    public String showAddScheduleForm(Model model) {
        model.addAttribute("schedule", new Schedule());
        return "schedule-add";
    }

    @PostMapping("/manage-schedules/add")
    public String addSchedule(Schedule schedule) {
        scheduleService.save(schedule);
        return "redirect:/manage-schedules";
    }

    @GetMapping("/manage-schedules/edit/{id}")
    public String showEditScheduleForm(@PathVariable("id") Long id, Model model) {
        Schedule schedule = scheduleService.findById(id);
        if (schedule == null) {
            model.addAttribute("msg", "Lịch trình không tồn tại.");
            return "redirect:/manage-schedules";
        }
        model.addAttribute("schedule", schedule);
        return "schedule-edit";
    }

    @PostMapping("/manage-schedules/edit/{id}")
    public String updateSchedule(@PathVariable("id") Long id, Schedule schedule) {
        schedule.setId(id);
        scheduleService.update(schedule);
        return "redirect:/manage-schedules";
    }

    @GetMapping("/manage-schedules/delete/{id}")
    public String deleteSchedule(@PathVariable("id") Long id) {
        scheduleService.delete(id);
        return "redirect:/manage-schedules";
    }
}