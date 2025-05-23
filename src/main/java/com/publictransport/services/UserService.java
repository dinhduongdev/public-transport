package com.publictransport.services;

import com.publictransport.dto.UserRegisterDTO;
import com.publictransport.models.User;
import jakarta.validation.ValidationException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

public interface UserService extends UserDetailsService {
    User getUserByEmail(String email);

    User register(UserRegisterDTO userRegisterDTO) throws ValidationException, IOException;

    boolean authenticate(String username, String password);
}
