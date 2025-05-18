package com.publictransport.services;

import com.publictransport.dto.UserRegisterDTO;
import com.publictransport.models.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService extends UserDetailsService {
    User getUserByEmail(String email);
    User register(Map<String,String> params, MultipartFile avatar);
    boolean authenticate(String username, String password);
}
