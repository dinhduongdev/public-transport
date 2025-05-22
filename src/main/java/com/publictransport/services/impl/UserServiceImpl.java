package com.publictransport.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.publictransport.models.User;
import com.publictransport.repositories.UserRepository;
import com.publictransport.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Service("userDetailsService")
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, Cloudinary cloudinary, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cloudinary = cloudinary;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserByEmail(String email) {
        return this.userRepository.getUserByEmail(email);
    }

    @Override
    public User register(Map<String, String> params, MultipartFile avatar) {
        User u = new User();
        u.setFirstname(params.get("firstName"));
        u.setLastname(params.get("lastName"));
        u.setEmail(params.get("email"));
        u.setPassword(this.passwordEncoder.encode(params.get("password")));
        u.setRole("USER");

        if (!avatar.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                u.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
            }
        }

        return this.userRepository.register(u);
    }

    @Override
    public boolean authenticate(String email, String password) {
        return this.userRepository.authenticate(email, password);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = this.getUserByEmail(username);
        if (u == null) {
            throw new UsernameNotFoundException("Invalid username.");
        }
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(u.getRole()));

        return new org.springframework.security.core.userdetails.User(
                u.getEmail(), u.getPassword(), authorities);
    }
}
