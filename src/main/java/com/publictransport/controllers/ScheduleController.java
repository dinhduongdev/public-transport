package com.publictransport.controllers;


import com.publictransport.models.Schedule;
import com.publictransport.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@Transactional
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;
    @GetMapping("manage-schedules")
    public String manageSchedules(Model model) {
        List<Schedule> schedules = this.scheduleService.getSchedules();

        model.addAttribute("schedules", schedules);
        return "schedule";
    }
}
