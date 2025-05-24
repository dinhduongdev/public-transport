package com.publictransport.repositories;

import com.publictransport.models.User;

public interface UserRepository {
    User getUserByEmail(String email);
//<<<<<<< HEAD
//    User register(User user);
    User update(User user);
//=======
    User createUser(User user);
//>>>>>>> 316244e0d60b925879b815cfd5da02ddea00e6e6
    boolean existsByEmail(String email);
    boolean authenticate(String email, String password);
}