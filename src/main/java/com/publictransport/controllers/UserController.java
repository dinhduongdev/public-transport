/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author duong
 */

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
