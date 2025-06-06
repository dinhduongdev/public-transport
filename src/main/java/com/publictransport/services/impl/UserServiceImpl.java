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
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


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
    public Optional<User> getUserByEmail(String email) {
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

    private User registerFromGoogle(OidcUser oidcUser) {
        if (oidcUser.getEmail() == null) {
            throw new IllegalArgumentException("Email from Google is null, cannot register user.");
        }

        User user = new User();
        user.setEmail(oidcUser.getEmail());
        user.setFirstname(oidcUser.getGivenName() != null ? oidcUser.getGivenName() : "");
        user.setLastname(oidcUser.getFamilyName() != null ? oidcUser.getFamilyName() : "");
        user.setAvatar(oidcUser.getPicture() != null ? oidcUser.getPicture() : "");
        user.setPassword(this.passwordEncoder.encode("google-oauth-" + oidcUser.getSubject()));
        user.setRole("USER");

        System.out.println("Registering Google user: " + user.getEmail());
        User registeredUser = this.userRepository.createUser(user);
        System.out.println("Google user registered: " + (registeredUser != null ? registeredUser.getEmail() : "null"));
        return registeredUser;
    }


    @Override
    public boolean authenticate(String email, String password) {
        return this.userRepository.authenticate(email, password);
    }

    @Override
    public User update(User user) {
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("User hoặc email không hợp lệ");
        }

        // Tìm người dùng để đảm bảo tồn tại
        Optional<User> userOpt = getUserByEmail(user.getEmail());
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng với email: " + user.getEmail());
        }
        User existingUser = userOpt.get();

        // Cập nhật thông tin người dùng
        existingUser.setFirstname(user.getFirstname());
        existingUser.setLastname(user.getLastname());
        existingUser.setAvatar(user.getAvatar());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(this.passwordEncoder.encode(user.getPassword()));
        }
        existingUser.setRole(user.getRole());

        // Lưu lại thông tin người dùng
        System.out.println("Updating user: " + existingUser.getEmail());
        return this.userRepository.update(existingUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = this.getUserByEmail(username);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("Invalid username.");
        }
        User user = userOpt.get();
        System.out.println("Loading user by username: " + user.getEmail());
//        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole()));
//        authorities.add(new SimpleGrantedAuthority(u.getRole()));
        List<GrantedAuthority> authorities = new ArrayList<>(
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
        authorities.add(new SimpleGrantedAuthority(user.getRole()));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities);
    }

    public OidcUser loadUserByOAuth2(OidcUserRequest userRequest) {
        // Dùng OidcUserService mặc định của Spring để lấy OidcUser
        OidcUserService delegate = new OidcUserService();
        OidcUser oidcUser = delegate.loadUser(userRequest);

        System.out.println("Processing OAuth2 user: " + oidcUser.getEmail());
        Optional<User> userOpt = this.getUserByEmail(oidcUser.getEmail());
        User user;

        if (userOpt.isEmpty()) {
            System.out.println("User not found, registering new Google user: " + oidcUser.getEmail());
            user = registerFromGoogle(oidcUser);
        } else {
            System.out.println("User already exists: " + userOpt.get().getEmail());
            user = userOpt.get();
        }

        List<GrantedAuthority> authorities = new ArrayList<>(
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
        authorities.add(new SimpleGrantedAuthority(user.getRole()));

        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
}
