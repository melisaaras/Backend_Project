package com.tpe.security.jwt;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//(AuthEntryPointJwt) amacı, Spring Security tabanlı uygulamalarda yetkisiz erişim denemelerini yönetmek ve bu durumda kullanıcılara uygun bir yanıt döndürmektir.


@Component // Spring tarafından bir bean olarak yönetilen bileşen olduğunu belirtir
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthEntryPointJwt.class);     // Logger tanımlaması, sınıfla ilgili loglamaları yapabilmek için kullanılır


    // commence() metodu, yetkisiz bir erişim olduğunda çalıştırılan koddur
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        LOGGER.error("UnAuthorized error : {}", authException.getMessage());  // Hata mesajı loglanır

        // Yanıt türü olarak JSON belirlenir
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // HTTP durumu "401 Unauthorized" olarak ayarlanır
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);


        // Yanıt gövdesi için bir map oluşturulur
        final Map<String,Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "UnAuthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());


        // ObjectMapper kullanılarak yanıt JSON formatında yazılır
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}


