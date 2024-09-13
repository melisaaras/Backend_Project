package com.tpe.security.jwt;
import com.tpe.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Spring Security ile JWT (JSON Web Token) tabanlı bir kimlik doğrulama mekanizması oluşturmak için bir AuthTokenFilter sınıfıdır. OncePerRequestFilter sınıfını genişletir, bu da her HTTP isteği için yalnızca bir kez çalıştırılacağı anlamına gelir. JWT'nin geçerliliğini doğrular ve eğer geçerliyse kullanıcı kimlik bilgilerini güvenlik bağlamına yerleştirir.
@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {


    //Hata ve bilgi mesajlarını loglamak için bir Logger nesnesi kullanılıyor.
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    private JwtUtils jwtUtils;


    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // her istek geldiğinde çağrılır. JWT'yi doğrulayıp geçerli bir kullanıcı olup olmadığını kontrol eder. Eğer geçerliyse kullanıcının kimliğini SecurityContextHolder'a ekler.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if(jwt !=null && jwtUtils.validateJwtToken(jwt)){

                String userName = jwtUtils.getUsernameFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                request.setAttribute("username", userName);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,null,userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (UsernameNotFoundException e) {
            LOGGER.error("Cannot set user authentication", e);
        }
        filterChain.doFilter(request,response);
    }


    //istek başlığından (Authorization header) JWT'yi ayıklar. JWT "Bearer " ile başlarsa, token'i alır ve geri döner. Aksi takdirde null döner.
    private String parseJwt(HttpServletRequest request){
        String headerAuth =  request.getHeader("Authorization"); // Merhaba
        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ") ){
            return headerAuth.substring(7);
        }
        return null;
    }
}