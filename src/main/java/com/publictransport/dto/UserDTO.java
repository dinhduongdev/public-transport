package com.publictransport.dto;

import com.publictransport.models.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String avatar;
    private String role;
    public UserDTO(User user) {
        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();
        this.role = user.getRole();
    }
}
