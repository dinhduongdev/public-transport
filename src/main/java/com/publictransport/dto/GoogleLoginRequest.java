package com.publictransport.dto;

import lombok.Data;

@Data
public class GoogleLoginRequest {
    private String email;
    private String name;
    private String avatar;
}