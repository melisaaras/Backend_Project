package com.tpe.controller.user;

import com.tpe.payload.messages.SuccessMessages;
import com.tpe.payload.request.LoginRequest;
import com.tpe.payload.request.UpdatePasswordRequest;
import com.tpe.payload.response.authentication.AuthResponse;
import com.tpe.service.user.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;



@RestController //Bu sınıfın bir Spring Rest Controller olduğunu belirtir. Bu, HTTP isteklerini işleyip HTTP cevapları döneceği anlamına gelir.
@RequestMapping("/auth") //Bu annotation, sınıfa gelen tüm isteklerin /auth ile başlaması gerektiğini ifade eder. Yani bu sınıf altında /login gibi alt yollar tanımlanabilir.
@RequiredArgsConstructor //final olarak tanımlanan alanlar için otomatik olarak bir constructor oluşturur. Böylece AuthenticationService nesnesinin bağımlılığı Spring tarafından constructor injection ile sağlanır.
public class AuthenticationController {



    private final AuthenticationService authenticationService;
    // Burada `AuthenticationService` sınıfına bir bağımlılık var. Bu servis, kullanıcının kimlik doğrulama işlemleriyle ilgilenecek.
    // `final` olarak tanımlandığı için bu alan, `@RequiredArgsConstructor` sayesinde constructor injection ile atanacak.


    // KULLANICILARIN SİSTEME GİRİŞ YAPMASINI SAĞLAMAK İÇİN KİMLİK DOĞRULAMA
    @PostMapping("/login") // http://localhost:8080/auth/login + POST
    // Bu annotation, "/login" URL'sine gelen POST isteklerinin bu metoda yönlendirilmesini sağlar. Yani http://localhost:8080/auth/login adresine yapılan bir POST isteği bu metoda düşecektir.
    // auth ve login path'iyle, POST olan request ile `LoginRequest` objesi (username ve password bilgileri) gelecek.
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody @Valid LoginRequest loginRequest){
        // ResponseEntity<AuthResponse>: Bu metodun dönüş tipi, HTTP cevabını içerir. AuthResponse türünde bir nesne döner ve bu nesne genellikle doğrulama sonrası kullanıcı bilgileri ve token gibi verileri içerir.
        // @RequestBody: Gelen HTTP isteği gövdesini alır ve `LoginRequest` sınıfına map eder. İstek gövdesi JSON olarak gelmelidir ve bu JSON, `LoginRequest` sınıfıyla uyumlu olmalıdır.
        // @Valid: `LoginRequest` üzerindeki validation kurallarının çalıştırılmasını sağlar. Örneğin, username ve password alanlarının belirli kurallara göre doğrulanmasını sağlar.
        return authenticationService.authenticateUser(loginRequest);
        // Bu satırda gelen loginRequest (username ve password bilgisi), authenticationService’e iletilir.
        // authenticationService.authenticateUser metodu çağrılır ve kullanıcı doğrulaması yapılır.
        // Dönüş tipi `AuthResponse` olacak (username, ssn, role, token, name gibi değerleri içerebilir).
        // Bu `AuthResponse`, bir `ResponseEntity` ile HTTP cevabı olarak geri gönderilir.
    }



    //3.ADIM: KULLANICININ PASSWORDÜNÜ GÜNCELLEME
    @PatchMapping("/updatePassword") // http://localhost:8080/auth/updatePassword  + Patch  + JSON
    // endpoint'e PATCH metodu kullanılarak bir istek yapılacaktır.PATCH istekleri genellikle bir kaynağın sadece bir kısmını (burada parolayı) güncellemek için kullanılır.
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')") //Updatepassword işlemini, anonim olmayan ve sisteme kayıtlı tüm kullanıcılar yapabilmeli. @PreAuthorize ile bu methodu tetikleyecek kişilerin roller belirtilir. hasAnyAuthority ile array gibi rolleri verirsiniz ve herhangi biriyle eşleşirse method çalışır
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest,
                                                 HttpServletRequest request){//İstek gövdesindeki (HTTP request body) JSON verisini alır ve bunu UpdatePasswordRequest classına dönüştürür. Bu classımız, güncellenmek istenen yeni parola ve eski parola gibi bilgileri içerir.
        //@Valid annotation'ı ile gelen istek doğrulanır. Örneğin, güncellenen parola boş olamaz gibi kurallar UpdatePasswordRequest classında tanımlanabilir.
        //HttpServletRequest request:Bu parametre, gelen HTTP isteğine dair ekstra bilgiler almak için kullanılır. Örneğin, IP adresi, başlık bilgileri (headers) gibi veriler alınabilir. Parola güncellerken, kimlik doğrulama işlemi için gerekli olabilir.

        authenticationService.updatePassword(updatePasswordRequest, request); //yukardaki parametrede argüman olarak iki datayı alır.
        String response = SuccessMessages.PASSWORD_CHANGED_RESPONSE_MESSAGE; //Parola başarılı bir şekilde güncellendikten sonra, başarılı güncelleme için bir yanıt mesajı
        return ResponseEntity.ok(response); //Parola başarılı bir şekilde güncellendikten sonra HTTP 200 (OK) yanıtı ile birlikte hazırlanan yanıt mesajı döndürülür.
    }






}