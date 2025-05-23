package com.publictransport.services.impl;

import com.cloudinary.utils.ObjectUtils;
import com.publictransport.dto.UserRegisterDTO;
import com.publictransport.models.User;
import com.publictransport.proxies.MediaFileProxy;
import com.publictransport.repositories.UserRepository;
import com.publictransport.services.UserService;
import jakarta.validation.ValidationException;
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
    private final MediaFileProxy cloudinaryProxy;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, MediaFileProxy cloudinaryProxy, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cloudinaryProxy = cloudinaryProxy;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserByEmail(String email) {
        return this.userRepository.getUserByEmail(email);
    }

    @Override
    public User register(UserRegisterDTO dto) throws ValidationException, IOException {
        if (this.userRepository.existsByEmail(dto.getEmail())) {
            throw new ValidationException("Email already exists");
        }

        User user = new User();
        user.setFirstname(dto.getFirstName());
        user.setLastname(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(this.passwordEncoder.encode(dto.getPassword()));
        user.setRole("USER");
        MultipartFile avatar = dto.getAvatar();
        if (avatar != null && !avatar.isEmpty()) {
            Map uploadResult = (Map) this.cloudinaryProxy.uploadFile(
                    avatar.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"
                    ));
            user.setAvatar(uploadResult.get("secure_url").toString());
        }

        return this.userRepository.createUser(user);
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
