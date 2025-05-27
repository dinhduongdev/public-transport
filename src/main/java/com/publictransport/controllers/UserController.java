package com.publictransport.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@CrossOrigin
public class UserController {
    @RequestMapping("/login")
    public String loginView(@RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (logout != null)
            model.addAttribute("msg", "Bạn đã đăng xuất thành công!");
        return "login";
    }
}
