package com.tpe.controller.user;

import com.tpe.payload.request.user.UserRequest;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.abstracts.BaseUserResponse;
import com.tpe.payload.response.user.UserResponse;
import com.tpe.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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



    @GetMapping("/getAllUserByPage/{userRole}") // http://localhost:8080/user/getAllUserByPage/Admin  + GET
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getUserByPage(
            @PathVariable String userRole,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "name") String sort,
            @RequestParam(value = "type",defaultValue = "desc") String type
    ){
        Page<UserResponse> adminsOrDeans = userService.getUsersByPage(page,size,sort,type,userRole);
        return new ResponseEntity<>(adminsOrDeans, HttpStatus.OK);
    }



    @GetMapping("/getUserById/{userId}") // http://localhost:8080/user/getUserById/1   + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<BaseUserResponse> getUserById(@PathVariable Long userId){
        return userService.getUserById(userId);
    }


    // Not : ODEV deleteUserById() ***************************************************




    // Not: updateAdminOrDeanOrViceDean() ********************************************
    // !!! Admin --> Dean veya  ViceDEan i guncellerken kullanilacak method
    // !!! Student ve teacher icin ekstra fieldlar gerekecegi icin, baska endpoint gerekiyor




    // Not: updateUserForUser() ******************************************************
    // !!! Kullanicinin kendisini update etmesini saglayan method
    // !!! AuthenticationController da updatePassword oldugu icin buradaki DTO da password olmamali




    // Not : getByName() *************************************************************





}
