package com.publictransport.controllers;


import com.publictransport.dto.GoogleLoginRequest;
import com.publictransport.dto.UserRegisterDTO;
import com.publictransport.models.User;
import com.publictransport.services.EmailService;
import com.publictransport.services.EmailVerificationService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class APIUserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;

    @Autowired
    public APIUserController(UserService userService, JwtUtils jwtUtils,
                             EmailService emailService, EmailVerificationService emailVerificationService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
        this.emailVerificationService = emailVerificationService;
    }
    // Hàm tạo mã xác thực
    private String generateVerificationCode() {
        int code = 100000 + new Random().nextInt(900000); // 6 chữ số
        return String.valueOf(code);
    }


    // 1. Gửi mã xác nhận đến email (dùng chung cho đăng ký email)
    @PostMapping("/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(@RequestParam("email") String email) {
        emailVerificationService.generateCodeForEmail(email); // Tạo mã mới, lưu trong bộ nhớ hoặc DB
        String code = emailVerificationService.getCode(email); // Lấy mã vừa tạo
        emailService.sendVerificationEmail(email, "Xác nhận đăng ký", "Mã xác nhận của bạn là: " + code);
        return ResponseEntity.ok("Đã gửi mã xác nhận đến email");
    }

    // 2. Đăng ký người dùng mới (cần mã xác nhận)
    @PostMapping(
            value = "/register/",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> register(
            @ModelAttribute @Valid UserRegisterDTO dto,
            @RequestParam("verificationCode") String verificationCode
    ) throws ValidationException, IOException {
        if (!emailVerificationService.verifyCode(dto.getEmail(), verificationCode)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã xác nhận không hợp lệ hoặc đã hết hạn");
        }
        emailVerificationService.removeCode(dto.getEmail());
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
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) throws Exception {
        String email = request.getEmail();
        // Tìm người dùng theo email
        User user = userService.getUserByEmail(email);
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setFirstName(request.getName() != null ? request.getName().split(" ")[0] : "");
        dto.setLastName(request.getName() != null && request.getName().split(" ").length > 1 ? request.getName().split(" ")[1] : "");
        dto.setEmail(email);
        dto.setPassword(UUID.randomUUID().toString());

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
    }

    @RequestMapping("/secure/profile")
    @ResponseBody
    public ResponseEntity<User> getProfile(Principal principal) {
        return new ResponseEntity<>(this.userService.getUserByEmail(principal.getName()), HttpStatus.OK);
    }

}