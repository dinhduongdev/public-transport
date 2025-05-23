package com.publictransport.repositories.impl;

import com.publictransport.models.User;
import com.publictransport.repositories.UserRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {
    private final SessionFactory factory;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserRepositoryImpl(SessionFactory factory, BCryptPasswordEncoder passwordEncoder) {
        this.factory = factory;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserByEmail(String email) {
        Session s = this.factory.getCurrentSession();
        Query q = s.createQuery("FROM User u WHERE u.email = :email", User.class);
        q.setParameter("email", email);
        try {
            return (User) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User createUser(User user) {
        Session s = this.factory.getCurrentSession();
        s.persist(user);
        s.refresh(user);
        return user;
    }

    @Override
    public boolean existsByEmail(String email) {
        Session s = this.factory.getCurrentSession();
        Query q = s.createQuery("SELECT COUNT(*) FROM User u WHERE u.email = :email", Long.class);
        q.setParameter("email", email);
        return (Long) q.getSingleResult() > 0;
    }

    @Override
    public boolean authenticate(String email, String password) {
        User u = this.getUserByEmail(email);
        return this.passwordEncoder.matches(password, u.getPassword());
    }
}
