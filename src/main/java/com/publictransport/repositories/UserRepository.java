package com.publictransport.repositories;

import com.publictransport.models.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> getUserByEmail(String email);

    User update(User user);

    User createUser(User user);

    boolean existsByEmail(String email);

    boolean authenticate(String email, String password);
    User findById(Long id);
}