package com.publictransport.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class UserRegisterDTO {
    @NotEmpty(message = "Họ không được để trống")
    @Size(max = 255, message = "Họ không được vượt quá 255 ký tự")
    private String firstName;

    @NotEmpty(message = "Tên không được để trống")
    @Size(max = 255, message = "Tên không được vượt quá 255 ký tự")
    private String lastName;

    @NotEmpty(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự")
    private String email;

    @NotEmpty(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 255, message = "Mật khẩu phải từ 6 đến 255 ký tự")
    private String password;

    private String role;

    private MultipartFile avatar;
}