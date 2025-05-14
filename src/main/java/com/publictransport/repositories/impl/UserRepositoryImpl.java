/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.repositories.impl;

import com.publictransport.models.User;
import com.publictransport.repositories.UserRepository;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author duong
 */
@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {
    private final BCryptPasswordEncoder passwordEncoder;
    private final SessionFactory FACTORY;

    @Autowired
    public UserRepositoryImpl(SessionFactory factory, BCryptPasswordEncoder passwordEncoder) {
        this.FACTORY = factory;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserByEmail(String email) {
        Session s = this.FACTORY.getCurrentSession();
        Query q = s.createQuery("FROM User u WHERE u.email = :email", User.class);
        q.setParameter("email", email);
        return (User) q.getSingleResult();
    }
}
