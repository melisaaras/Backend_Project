package com.tpe.service.user;
import com.tpe.entity.concretes.user.User;
import com.tpe.exception.BadRequestException;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.payload.request.LoginRequest;
import com.tpe.payload.request.UpdatePasswordRequest;
import com.tpe.payload.response.authentication.AuthResponse;
import com.tpe.repository.user.UserRepository;
import com.tpe.security.jwt.JwtUtils;
import com.tpe.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

//Kullanıcı Doğrulama (Authentication):
//JWT Token Oluşturma:
//Kullanıcı Bilgilerini ve Rolleri Dönme:
//HTTP Yanıtı Oluşturma:

@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final UserRepository userRepository;
    // `UserRepository`, veritabanında kullanıcılarla ilgili işlemleri yapmak için kullanılır.


    private final AuthenticationManager authenticationManager;
    // `AuthenticationManager`, Spring Security'den gelir ve kullanıcının kimlik doğrulama işlemlerini yönetir (kullanıcı adı ve şifre ile).

    private final JwtUtils jwtUtils;    // `JwtUtils`, JWT token üretimi ve doğrulama işlemleriyle ilgilenir.

    private final PasswordEncoder passwordEncoder;



    public ResponseEntity<AuthResponse> authenticateUser(LoginRequest loginRequest) {  // `LoginRequest` (kullanıcı adı ve şifre) alarak kullanıcının kimliğini doğrular ve başarı durumunda `AuthResponse` döner.

        // anlık olarak login işlemini gerçekleştiren kullanıcının gönderdiği username ve password bilgilerini alıyoruz.
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
        // `AuthenticationManager` kullanarak kullanıcının kimlik doğrulaması yapılır. `UsernamePasswordAuthenticationToken`, kullanıcı adı ve şifre ile doğrulama yapmak için kullanılır.

        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Doğrulama başarılıysa, güvenlik bilgileri `SecurityContext`'e set edilir. Bu, sistemdeki güvenlik bağlamını (kullanıcı kimliği, rolleri vs.) tutar.


        String token = "Bearer " + jwtUtils.generateJwtToken(authentication);
        // JWT token üretilir ve "Bearer " önekiyle birlikte saklanır. Bu token, kullanıcının sonraki isteklerde kimlik doğrulaması için kullanacağı yetkilendirme bilgisi olur.


        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();        // `UserDetailsImpl`, kullanıcıya ait detayların (kullanıcı adı, roller, vb.) tutulduğu özel bir class. .getPrincipal() anlık olarak login işlemini gerçekleştiren nesneyi verir.framework login yapanın bir user olduğunu bilmediği için nesne olarak döndürür. getprincipal, object classından türediği için UserDetailsImpl classına cast yapmamız gerekti.



        // kullanıcının sahip olduğu GrantedAuthority nesnelerinden (yetkilerden), getAuthority() metoduyla her bir yetkinin adını (örneğin, ROLE_USER, ROLE_ADMIN) String olarak alıyoruz. Sonrasında, bu String rollerini bir Set<String> içinde topluyoruz. Set kullanarak, aynı rollerin birden fazla kez eklenmesi engelleniyor.
        Set<String> roles = userDetails.getAuthorities()// userDetails: Bu, kullanıcının kimlik bilgilerini içeren bir nesnedir.getAuthorities, kullanıcının yetkilerini (rollerini) döner, collection olarak döndüğü için Set olarak karşılıyoruz.
                .stream()
                .map(GrantedAuthority::getAuthority) // kullanıcının sahip olduğu GrantedAuthority nesnelerinden (yetkilerden), getAuthority() metoduyla her bir yetkinin adını (örneğin, ROLE_USER, ROLE_ADMIN) String olarak alıyoruz.
                .collect(Collectors.toSet()); //Sonrasında, bu String rollerini bir Set<String> içinde topluyoruz. Set kullanarak, aynı rollerin birden fazla kez eklenmesi engelleniyor.



        Optional<String> role = roles.stream().findFirst();// Rollerden ilk bulunan rol seçilir.roles.getzero() da aynı işlemi yapar.


        AuthResponse.AuthResponseBuilder authResponse =  AuthResponse.builder(); // JWT token ve kullanıcı bilgilerini içerecek olan `AuthResponse.builder()` ile `AuthResponse` nesnesi inşa edilmeye başlanır.

        authResponse.username(userDetails.getUsername()); // Kullanıcının adı `AuthResponse`'a set edilir.

        authResponse.token(token.substring(7)); // Token'dan "Bearer " kısmı çıkarılarak `AuthResponse`'a set edilir.

        authResponse.name(userDetails.getName()); // Kullanıcının name `AuthResponse`'a eklenir.

        authResponse.ssn(userDetails.getSsn()); // Kullanıcının ssnsi `AuthResponse`'a eklenir.


        role.ifPresent(authResponse::role); //Eğer optional yapıdaki rolün içinde bir nesne varsa, yukardaki authresponseın rol methoduna argüman olarak gönder.ifPresent,eğer varsa demektir.


        return ResponseEntity.ok(authResponse.build()); // `AuthResponse` nesnesi build ile tamamlanır ve HTTP 200 OK cevabıyla birlikte geri döndürülür. Bu cevap, kullanıcıya doğrulama sonucunu ve JWT token'ı verir.

    }


    // 123456
    public void updatePassword(UpdatePasswordRequest updatePasswordRequest, HttpServletRequest request) {

        String userName = (String) request.getAttribute("username"); // Kullanıcının usernamei HTTP request üzerinden alır.

        User user = userRepository.findByUsernameEquals(userName);// Kullanıcı adını kullanarak kullanıcıyı DBden alır.


        if(! passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())){
            throw new BadRequestException(ErrorMessages.PASSWORD_NOT_MATCHED);
        } //Kullanıcının mevcut şifresi, kullanıcının gönderdiği eski şifreyle eşleşiyor mu kontrol eder. Eğer eşleşmiyorsa, BadRequestException fırlatılır. passwordEncoder classı, passwordün hashini alıp, DBdeki hash ile karşılaştırmamızı sağlar. matches() methodu, iki parametre alır. ilk parametrede kullanıcıdan gelen string password, ikinci parametre DBdeki hashli passworddür.


        if(Boolean.TRUE.equals(user.getBuilt_in())){  // user.getBuilt_in.equals( Boolean.TRUE); null safe olmadığı için bu şekilde kullanılmadı.
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        } //Eğer kullanıcı, sisteme entegre (built-in) olarak işaretlenmişse, yani Boolean.TRUE değerine sahipse,şifre değiştirme işlemi yapılmamalıdır ve BadRequestException fırlatılır.


        String hashedPassword = passwordEncoder.encode(updatePasswordRequest.getNewPassword());
        user.setPassword(hashedPassword); //Yeni şifreyi hash'leyip (şifreleme) veritabanındaki kullanıcı nesnesine set eder.

        userRepository.save(user); //Güncellenmiş kullanıcı nesnesini veritabanına kaydeder.


    }
}