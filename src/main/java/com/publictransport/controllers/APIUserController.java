package com.publictransport.controllers;


import com.publictransport.dto.UserRegisterDTO;
import com.publictransport.models.User;
import com.publictransport.services.UserService;
import com.publictransport.utils.JwtUtils;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;

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

    @PostMapping(
            value = "/register/",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> register(
            @ModelAttribute @Valid UserRegisterDTO dto
    ) throws ValidationException, IOException {
        return new ResponseEntity<>(this.userService.register(dto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User u) throws Exception {
        if (!this.userService.authenticate(u.getEmail(), u.getPassword()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai thông tin đăng nhập");
        String token = jwtUtils.generateToken(u.getEmail());
        return ResponseEntity.ok().body(Collections.singletonMap("token", token));
    }

    @RequestMapping("/secure/profile")
    @ResponseBody
    public ResponseEntity<User> getProfile(Principal principal) {
        return new ResponseEntity<>(this.userService.getUserByEmail(principal.getName()), HttpStatus.OK);
    }

}
