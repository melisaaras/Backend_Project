package com.tpe.controller.user;

import com.tpe.payload.request.user.UserRequest;
import com.tpe.payload.request.user.UserRequestWithoutPassword;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

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
        //userRequest: Kullanıcıya ait bilgileri içeren bir DTO (Data Transfer Object) nesnesidir. İstemciden gelen JSON verileri bu nesneye dönüştürülür.
        //userRole: URL'den alınan dinamik bir parametredir (örneğin, "Admin" ya da "User"). Kullanıcının rolünü belirtir ve işleme dahil edilir.
    }



    //  belirli bir rol için kullanıcıların sayfalı listesini döndürmek amacıyla yazılmıştır. @PreAuthorize ile yalnızca ADMIN yetkisine sahip kullanıcılar bu endpoint'e erişebilir.
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



    // bir kullanıcının ID'sine göre kullanıcı bilgilerini döndürmek için kullanılır.
    //@PreAuthorize ile sadece ADMIN ve MANAGER yetkisine sahip kullanıcıların bu endpoint'e erişmesi sağlanır.
    @GetMapping("/getUserById/{userId}") // http://localhost:8080/user/getUserById/1   + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<BaseUserResponse> getUserById(@PathVariable Long userId){
        return userService.getUserById(userId);
    }


    // Not : ODEV deleteUserById() ***************************************************
    @DeleteMapping("/delete/{id}") //http://localhost:8080/user/delete/3
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id ,
                                                 HttpServletRequest httpServletRequest){
        return ResponseEntity.ok(userService.deleteUserById(id , httpServletRequest));
    }//deleteUserById(id, httpServletRequest) metodu, belirtilen id'ye sahip kullanıcıyı veri tabanından silme işlemini gerçekleştirir. İşlem sırasında, örneğin:Kullanıcı veri tabanında var mı kontrolü yapılır.Silme işlemi başarıyla tamamlanırsa, bir sonuç döndürülür (örneğin bir mesaj veya durum bilgisi).



    // Not: updateAdminOrDeanOrViceDean() ********************************************
    // !!! Admin --> Dean veya  ViceDEan i guncellerken kullanilacak method
    // !!! Student ve teacher icin ekstra fieldlar gerekecegi icin, baska endpoint gerekiyor
    @PutMapping("/update/{userId}") // http://localhost:8080/user/update/1
    @PreAuthorize("hasAuthority('ADMIN')")
    //!!! donen deger BaseUserResponse --> polymorphism
    public ResponseMessage<BaseUserResponse> updateAdminDeanViceDeanForAdmin(
            @RequestBody @Valid UserRequest userRequest,
            @PathVariable Long userId){
        return userService.updateUser(userRequest,userId);
    }

    // Not: updateUserForUser() ******************************************************
    // !!! Kullanicinin kendisini update etmesini saglayan method
    // !!! AuthenticationController da updatePassword oldugu icin buradaki DTO da password olmamali
    @PatchMapping("/updateUser")   // http://localhost:8080/user/updateUser
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public ResponseEntity<String>updateUser(@RequestBody @Valid
                                                UserRequestWithoutPassword userRequestWithoutPassword,
                                            HttpServletRequest request){
        return userService.updateUserForUsers(userRequestWithoutPassword, request);
    }

    // Not : getByName() *************************************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/getUserByName")   // http://localhost:8080/user/getUserByName?name=user1
    public List<UserResponse> getUserByName(@RequestParam (name = "name") String userName){
        return userService.getUserByName(userName);
    }





}
