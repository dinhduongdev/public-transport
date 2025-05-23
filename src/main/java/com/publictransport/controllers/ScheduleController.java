package com.publictransport.controllers;

import com.publictransport.dto.ScheduleDTO;
import com.publictransport.models.*;
import com.publictransport.services.RouteVariantService;
import com.publictransport.services.ScheduleDayService;
import com.publictransport.services.ScheduleService;
import com.publictransport.services.ScheduleTripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleTripService scheduleTripService;
    @Autowired
    private RouteVariantService routeVariantService;
    @Autowired
    private ScheduleDayService scheduleDayService;



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
        model.addAttribute("scheduleDTO", new ScheduleDTO());
        model.addAttribute("routeVariants", routeVariantService.findAllRouteVariants(1, Integer.MAX_VALUE));
        return "schedule/schedule-add";
    }

    @PostMapping("/manage-schedules/add")
    public String addSchedule(@ModelAttribute ScheduleDTO scheduleDTO,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        try {
            // Lưu Schedule
            scheduleService.saveFromDTO(scheduleDTO);
            redirectAttributes.addFlashAttribute("success", "Thêm lịch trình thành công!");
            return "redirect:/manage-schedules";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("scheduleDTO", scheduleDTO);
            model.addAttribute("routeVariants", routeVariantService.findAllRouteVariants(1,Integer.MAX_VALUE));
            return "schedule/schedule-add";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi thêm lịch trình: " + e.getMessage());
            model.addAttribute("scheduleDTO", scheduleDTO);
            model.addAttribute("routeVariants", routeVariantService.findAllRouteVariants(1, Integer.MAX_VALUE));
            return "schedule/schedule-add";
        }
    }

    @GetMapping("/manage-schedules/edit/{id}")
    public String showEditScheduleForm(@PathVariable("id") Long id, Model model) {
        Schedule schedule = scheduleService.findById(id);
        // Chuyển Schedule thành ScheduleDTO
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(id);
        scheduleDTO.setRouteVariantId(schedule.getRouteVariant() != null ? schedule.getRouteVariant().getId() : null);
        scheduleDTO.setStartDate(schedule.getStartDate());
        scheduleDTO.setEndDate(schedule.getEndDate());
        scheduleDTO.setPriority(schedule.getPriority());

        // Lấy danh sách ScheduleTrip
        List<ScheduleTrip> trips = scheduleTripService.findByScheduleId(id, 1, Integer.MAX_VALUE);
        scheduleDTO.setStartTimes(trips.stream().map(trip -> trip.getStartTime().toString()).collect(Collectors.toList()));
        scheduleDTO.setEndTimes(trips.stream().map(trip -> trip.getEndTime().toString()).collect(Collectors.toList()));
        scheduleDTO.setLicenses(trips.stream().map(trip -> trip.getLicense() != null ? trip.getLicense() : "").collect(Collectors.toList()));


        model.addAttribute("scheduleDTO", scheduleDTO);
        model.addAttribute("routeVariants", routeVariantService.findAllRouteVariants(1, Integer.MAX_VALUE));
        return "schedule/schedule-edit";
    }

    @PostMapping("/manage-schedules/edit/{id}")
    public String updateSchedule(@PathVariable("id") Long id,
                                 @ModelAttribute ScheduleDTO scheduleDTO,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Cập nhật lịch trình
            scheduleDTO.setId(id);
            scheduleService.updateFromDTO(scheduleDTO);
            // Thêm flash attribute cho thông báo thành công
            redirectAttributes.addFlashAttribute("success", "Cập nhật lịch trình thành công!");

            return "redirect:/manage-schedules";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("scheduleDTO", scheduleDTO);
            model.addAttribute("routeVariants", routeVariantService.findAllRouteVariants(1, Integer.MAX_VALUE));
            return "schedule/schedule-edit";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi cập nhật lịch trình: " + e.getMessage());
            model.addAttribute("scheduleDTO", scheduleDTO);
            model.addAttribute("routeVariants", routeVariantService.findAllRouteVariants(1, Integer.MAX_VALUE ));
            return "schedule/schedule-edit";
        }
    }

    @GetMapping("/manage-schedules/delete/{id}")
    public String deleteSchedule(@PathVariable("id") Long id) {
        scheduleService.delete(id);
        return "redirect:/manage-schedules";
    }
}