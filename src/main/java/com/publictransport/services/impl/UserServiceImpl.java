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
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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

//    @Override
//<<<<<<< HEAD
//    public User register(Map<String, String> params, MultipartFile avatar) {
//        User u = new User();
//        u.setFirstname(params.get("firstName"));
//        u.setLastname(params.get("lastName"));
//        u.setEmail(params.get("email"));
//        u.setPassword(this.passwordEncoder.encode(params.get("password")));
//        u.setRole("USER");
//
//        if (avatar != null && !avatar.isEmpty()) {
//            try {
//                Map res = cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
//                u.setAvatar(res.get("secure_url").toString());
//            } catch (IOException ex) {
//            }
//=======
    @Override
    public User register(UserRegisterDTO dto) throws ValidationException, IOException {
        if (this.userRepository.existsByEmail(dto.getEmail())) {
            throw new ValidationException("Email already exists");
//>>>>>>> 316244e0d60b925879b815cfd5da02ddea00e6e6
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
        User existingUser = getUserByEmail(user.getEmail());
        if (existingUser == null) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng với email: " + user.getEmail());
        }

        // Cập nhật thông tin người dùng
        existingUser.setFirstname(user.getFirstname());
        existingUser.setLastname(user.getLastname());
        existingUser.setAvatar(user.getAvatar());
        if (user.getPassword() != null) {
            existingUser.setPassword(this.passwordEncoder.encode(user.getPassword()));
        }
        existingUser.setRole(user.getRole());

        // Lưu lại thông tin người dùng
        System.out.println("Updating user: " + existingUser.getEmail());
        return this.userRepository.update(existingUser);
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

    public OidcUser loadUserByOAuth2(OidcUserRequest userRequest) {
        // Dùng OidcUserService mặc định của Spring để lấy OidcUser
        OidcUserService delegate = new OidcUserService();
        OidcUser oidcUser = delegate.loadUser(userRequest);

        System.out.println("Processing OAuth2 user: " + oidcUser.getEmail());
        User user = this.getUserByEmail(oidcUser.getEmail());

        if (user == null) {
            System.out.println("User not found, registering new Google user: " + oidcUser.getEmail());
            registerFromGoogle(oidcUser);
        } else {
            System.out.println("User already exists: " + user.getEmail());
        }

        return oidcUser;
    }
}
