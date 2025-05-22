package com.publictransport.controllers;


import com.publictransport.models.User;
import com.publictransport.services.UserService;
import com.publictransport.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class APIUserController {
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Autowired
    public APIUserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping(value = "/users",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> register(@RequestParam Map<String, String> params, @RequestParam(value = "avatar") MultipartFile avatar) {
        return new ResponseEntity<>(this.userService.register(params, avatar), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User u) {
        if (!this.userService.authenticate(u.getEmail(), u.getPassword()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai thông tin đăng nhập");
        try {
            String token = jwtUtils.generateToken(u.getEmail());
            return ResponseEntity.ok().body(Collections.singletonMap("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi tạo JWT");
        }
    }

    @RequestMapping("/secure/profile")
    @ResponseBody
    public ResponseEntity<User> getProfile(Principal principal) {
        return new ResponseEntity<>(this.userService.getUserByEmail(principal.getName()), HttpStatus.OK);
    }

}
