package com.publictransport.controllers;

import com.publictransport.dto.NotificationDTO;
import com.publictransport.models.User;
import com.publictransport.repositories.UserRepository;
import com.publictransport.services.NotificationService;
import com.publictransport.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class APINotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;


    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUserId(@PathVariable("userId") Long userId) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{notificationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> markNotificationAsRead(
            @PathVariable("notificationId") Long notificationId,
            HttpServletRequest request
    ) {
        try {
            Long userId = getUserIdFromRequest(request);
            if(userId == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Vui lòng đăng nhập để sử dụng tính năng này");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            notificationService.markNotificationAsRead(notificationId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Không thể thể đánh dấu thông báo");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }


    }



    private Long getUserIdFromRequest(HttpServletRequest request) {
        try {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                String email = jwtUtils.validateTokenAndGetEmail(token);

                if (email != null) {
                    Optional<User> userOpt = userRepository.getUserByEmail(email);
                    if (userOpt.isPresent()) {
                        return userOpt.get().getId();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
