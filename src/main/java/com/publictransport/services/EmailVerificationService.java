package com.publictransport.services;

public interface EmailVerificationService {
    void generateCodeForEmail(String email);
    boolean verifyCode(String email, String code);
    void removeCode(String email);
    String getCode(String email);
}
