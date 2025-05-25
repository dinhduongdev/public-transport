package com.publictransport.services.impl;

import com.publictransport.services.EmailVerificationService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;



@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    @Override
    public void generateCodeForEmail(String email) {
        // random 6 chữ số
        String code = String.valueOf(new Random().nextInt(900000) + 100000);
        verificationCodes.put(email, code);
    }

    @Override
    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email);
        return storedCode != null && storedCode.equals(code);
    }

    @Override
    public void removeCode(String email) {
        verificationCodes.remove(email);
    }

    @Override
    public String getCode(String email) {
        return verificationCodes.get(email);
    }
}
