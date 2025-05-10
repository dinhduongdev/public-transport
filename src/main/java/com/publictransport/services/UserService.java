package com.publictransport.services;

import com.publictransport.models.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User getUserByEmail(String email);
}
