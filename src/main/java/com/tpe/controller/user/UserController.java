package com.tpe.controller.user;

import com.tpe.payload.request.user.UserRequest;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.user.UserResponse;
import com.tpe.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController //requestleri http ile karşılayacağız
@RequestMapping("/user") //user path ile başlayan requestler gelsin
@RequiredArgsConstructor //service katmanıyla injectionlarımı yapabilmek için
public class UserController {

    private final UserService userService;

    @PostMapping("/save/{userRole}") // http://localhost:8080/user/save/Admin  + POST + JSON // HTTP POST isteği ile /save/{userRole} yoluna yapılan çağrılar bu metoda yönlendirilir.
    @PreAuthorize("hasAnyAuthority('ADMIN')") // Sadece 'ADMIN' yetkisine sahip kullanıcılar bu metoda erişebilir.
    public ResponseEntity<ResponseMessage<UserResponse>> saveUser(
            @RequestBody @Valid UserRequest userRequest, @PathVariable String userRole){// URL'den dinamik olarak alınan userRole parametresi metoda iletilir.

        return ResponseEntity.ok(userService.saveUser(userRequest, userRole)); // userService.saveUser(userRequest, userRole) çağrısı, kullanıcıyı kaydeder ve UserResponse nesnesini döner.
        // Bu yanıt, HTTP 200 OK durum kodu ile birlikte ResponseEntity olarak döndürülür.
    }
}
