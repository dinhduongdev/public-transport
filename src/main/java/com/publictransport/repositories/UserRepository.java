package com.publictransport.repositories;

import com.publictransport.models.User;

public interface UserRepository {
    User getUserByEmail(String email);
    User createUser(User user);
    boolean existsByEmail(String email);
    boolean authenticate(String email, String password);
}