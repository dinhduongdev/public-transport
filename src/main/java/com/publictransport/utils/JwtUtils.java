package com.publictransport.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.util.Date;

public class JwtUtils {
    private final String SECRET;
    private final long EXPIRATION_MS;
    private final JWSAlgorithm ALGORITHM;
    private final JWSSigner SIGNER;
    private final JWSVerifier VERIFIER;
    private final JWSHeader HEADER;

    public JwtUtils(String secret, long expirationMs) throws JOSEException {
        this.SECRET = secret;
        this.EXPIRATION_MS = expirationMs;
        this.ALGORITHM = JWSAlgorithm.HS256;
        this.SIGNER = new MACSigner(SECRET);
        this.VERIFIER = new MACVerifier(SECRET);
        this.HEADER = new JWSHeader(ALGORITHM);
    }

    public String generateToken(String username) throws Exception {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .issueTime(new Date())
                .build();

        SignedJWT signedJWT = new SignedJWT(this.HEADER, claimsSet);

        signedJWT.sign(this.SIGNER);

        return signedJWT.serialize();
    }

    public String validateTokenAndGetEmail(String token) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token);

        // Check if the token is signed with the correct algorithm
        if (!signedJWT.verify(this.VERIFIER))
            return null;

        // Check if the token is expired
        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (!expiration.after(new Date()))
            return null;

        return signedJWT.getJWTClaimsSet().getSubject();
    }
}