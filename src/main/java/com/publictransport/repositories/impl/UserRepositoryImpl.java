package com.publictransport.repositories.impl;

import com.publictransport.models.User;
import com.publictransport.repositories.UserRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public Optional<User> getUserByEmail(String email) {
        Session s = this.factory.getCurrentSession();
        org.hibernate.query.Query<User> q = s.createQuery("FROM User u WHERE u.email = :email", User.class);
        q.setParameter("email", email);
        return Optional.ofNullable(q.getSingleResultOrNull());
    }

    @Override
    public User createUser(User user) {
        Session s = this.factory.getCurrentSession();
        s.persist(user);
        s.refresh(user);
        return user;
    }

    @Override
    public User update(User user) {
        Session s = this.factory.getCurrentSession();
        // Kiểm tra xem user đã tồn tại chưa
        if (user.getEmail() == null || !existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Không tìm thấy người dùng với email " + user.getEmail());
        }
        s.merge(user);
        s.flush();
        s.refresh(user);
        return user;
    }

    public boolean existsByEmail(String email) {
        Session s = this.factory.getCurrentSession();
        Query q = s.createQuery("SELECT COUNT(*) FROM User u WHERE u.email = :email", Long.class);
        q.setParameter("email", email);
        return (Long) q.getSingleResult() > 0;
    }

    @Override
    public boolean authenticate(String email, String password) {
        Optional<User> u = this.getUserByEmail(email);
        if (u.isEmpty()) {
            return false;
        }
        return this.passwordEncoder.matches(password, u.get().getPassword());
    }

    @Override
    public User findById(Long id) {
        Session s = this.factory.getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root<User> root = q.from(User.class);
        q.select(root);

        q.where(b.equal(root.get("id").as(Long.class), id));

        Query query = s.createQuery(q);
        return (User) query.getSingleResult();
    }
}
