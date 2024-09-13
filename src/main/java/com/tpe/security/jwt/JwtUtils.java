package com.tpe.security.jwt;

import com.tpe.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;

//JWT (JSON Web Token) oluşturma ve doğrulama işlevlerini sağlar. Sınıf, Spring’in @Component notasyonu ile işaretlenmiş, böylece Spring tarafından bir bileşen olarak yönetilir.

@Component
public class JwtUtils {


    //Hata ve bilgi mesajlarını loglamak için bir Logger nesnesi kullanılır.
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);


    @Value("${backendapi.app.jwtExpirationMs}")
    private long jwtExpirationMs;


    @Value("${backendapi.app.jwtSecret}")
    private String jwtSecret;


    // Not: Generate JWT ***************************************************


    //kimlik doğrulama işlemi sırasında kullanıcı adı kullanarak bir JWT oluşturur.
    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return generateTokenFromUsername(userDetails.getUsername());

    }


    // kullanıcı adını bir JWT'ye dönüştürür ve bu token'ı geri döner. Token, kullanıcı adını (setSubject), oluşturulma tarihini (setIssuedAt) ve sona erme tarihini (setExpiration) içerir. Token, HS512 algoritması ile imzalanır.
    private String generateTokenFromUsername(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret) //jwtSecret, tokena özel belirlemiş olduğumuz anahtar
                .compact();
    }


    // Not: Validate JWT ***************************************************


    //JWT token'larının doğruluğunu sağlamanın yanı sıra, herhangi bir doğrulama hatası olduğunda ilgili bilgiyi loglar, bu da hata ayıklamayı kolaylaştırır.
    public boolean validateJwtToken(String jwtToken){

        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken);
            return true;
        } catch (ExpiredJwtException e) {
            LOGGER.error("Jwt token is expired : {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("Jwt token is unsupported : {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Jwt token is invalid : {}", e.getMessage());
        } catch (SignatureException e) {
            LOGGER.error("Jwt Signature is invalid : {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Jwt token is empty : {}", e.getMessage());
        }
        return false;
    }


    // Not: getUsernameFromJWT *********************************************

    // JWT'den kullanıcı adını alır. Token'ı çözerek kullanıcı adını (getSubject) geri döner.
    public String getUsernameFromJwtToken(String token){
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
