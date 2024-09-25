package com.tpe.security.config;

import com.tpe.security.jwt.AuthEntryPointJwt;
import com.tpe.security.jwt.AuthTokenFilter;
import com.tpe.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//amacı, Spring Security kullanarak uygulamanın güvenlik yapılandırmasını sağlamaktır. Bu sınıf, uygulamanızda kullanıcı doğrulama, yetkilendirme, oturum yönetimi, CORS (Cross-Origin Resource Sharing) ve JWT (JSON Web Token) gibi güvenlik yapılandırmalarını özelleştirmenizi sağlar.

@EnableWebSecurity // Web güvenliği etkinleştiriliyor
@Configuration // Bean tanımlamaları için Configuration ekleniyor
@EnableGlobalMethodSecurity(prePostEnabled = true) // Yöntem bazlı güvenlik sağlamak için global method security etkinleştiriliyor
@RequiredArgsConstructor // Constructor bağımlılık enjeksiyonu için final alanları dolduran Lombok anotasyonu
public class WebSecurityConfig {

    private final AuthEntryPointJwt unauthorizedHandler;    // Yetkisiz erişim girişimlerinde kullanıcıya dönecek hatayı yöneten handler

    private final UserDetailsServiceImpl userDetailsService;    // Kullanıcı detaylarını sağlayan servis, genellikle kullanıcı adıyla sorgulama yapar


    // AuthenticationManager bean'i, AuthenticationConfiguration'dan alınır ve kimlik doğrulama yönetimi sağlar
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    // SecurityFilterChain: HTTP güvenlik yapılandırmalarını belirler
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {         // CORS ayarları ve CSRF (Cross-Site Request Forgery) koruması kapatılır
        http.cors().and()
                .csrf().disable() // Yetkisiz giriş denemelerinde hata işleyicisi atanır
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()                 // Session yönetimi STATELESS olarak ayarlanır, yani her istek bağımsızdır (JWT ile çalışmak için gereklidir)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()                 // Beyaz listeye alınan URL'lere izin verilir, diğer tüm istekler kimlik doğrulaması gerektirir
                .authorizeRequests().antMatchers(AUTH_WHITE_LIST).permitAll()
                .anyRequest().authenticated();
        http.headers().frameOptions().sameOrigin(); // Frame-Options: Aynı kaynaktan gelen çerçeveler için izin verilir (örneğin, H2 konsolu kullanıyorsanız)
        http.authenticationProvider(authenticationProvider());// DaoAuthenticationProvider kimlik doğrulama sağlayıcısı olarak eklenir
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);// JWT doğrulama filtresi, UsernamePasswordAuthenticationFilter'dan önce eklenir


        return http.build();
    }


    // JWT doğrulama işlemlerini yapan filtreyi oluşturur
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter(){
        return new AuthTokenFilter();
    }


    // Şifreleme için BCryptPasswordEncoder bean'i tanımlanır
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    // Kullanıcı doğrulama sağlayıcısı olarak DaoAuthenticationProvider tanımlanır ve şifreleme yöntemi ile kullanıcı detay servisi atanır
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userDetailsService);
        return authenticationProvider;
    }



    // Beyaz listeye alınan ve kimlik doğrulaması gerektirmeyen URL'ler
    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedHeaders("*")
                        .allowedMethods("*");
            }
        };
    }



    private static final String[] AUTH_WHITE_LIST = {
            "index.html", //ana sayfa
            "/image/**", //resim dosyaları
            "/css/**", //CSS dosyaları
            "/js/**", //JavaScript dosyaları
            "/contactMessage/save", // İletişim mesajı kaydetme endpoint'i
            "/auth/login", // Giriş yapma endpoint'i
            "/" ,//Ana URL
            "/v3/api-docs/**", // eklenecek
            "swagger-ui.html", // eklenecek
            "/swagger-ui/**", // eklenecek
    };
}