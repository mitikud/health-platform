package com.medical.apigateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Base64;

@Service
public class JwtService {

    // For HMAC (shared secret), swap to Keys.hmacShaKeyFor(...)
    public PublicKey toRsaPublicKeyFromPem(String pem) {
        try {
            String clean = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] der = Base64.getDecoder().decode(clean);
            java.security.spec.X509EncodedKeySpec spec = new java.security.spec.X509EncodedKeySpec(der);
            return java.security.KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception e) { throw new RuntimeException("Invalid JWT public key", e); }
    }

    public Jws<Claims> parse(String token, PublicKey key) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public String getSubject(Claims c) { return c.getSubject(); }           // userId
    public String getRole(Claims c)     { return (String) c.get("role"); }
}
