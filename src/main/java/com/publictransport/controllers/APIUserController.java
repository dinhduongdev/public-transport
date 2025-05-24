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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
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

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request){
        try {
            String email = request.getEmail();
            // Tìm người dùng theo email
            User user = userService.getUserByEmail(email);
            UserRegisterDTO dto = new UserRegisterDTO();
            dto.setFirstName(request.getName() != null ? request.getName().split(" ")[0] : "");
            dto.setLastName(request.getName() != null && request.getName().split(" ").length > 1 ? request.getName().split(" ")[1] : "");
            dto.setEmail(email);
            dto.setPassword("google-oauth-" + email);

            if (user == null) {
                // Nếu không tồn tại, đăng ký người dùng mới
                dto.setRole("USER");
                user = userService.register(dto);
            }
            // Cập nhật avatar từ Google
            user.setAvatar(request.getAvatar());
            user = userService.update(user);
            // Tạo token JWT
            String token = jwtUtils.generateToken(user.getEmail());

            // Đặt thông tin xác thực vào SecurityContext để tạo phiên (session)
            Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), null, userService.loadUserByUsername(user.getEmail()).getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            // Trả về token cho frontend
            return ResponseEntity.ok().body(Collections.singletonMap("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xử lý đăng nhập Google: " + e.getMessage());
        }
    }

    @RequestMapping("/secure/profile")
    @ResponseBody
    public ResponseEntity<User> getProfile(Principal principal) {
        return new ResponseEntity<>(this.userService.getUserByEmail(principal.getName()), HttpStatus.OK);
    }

}
class GoogleLoginRequest {
    private String email;
    private String name;
    private String avatar;

    // Getters và setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}