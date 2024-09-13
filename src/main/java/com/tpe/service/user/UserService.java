package com.tpe.service.user;

import com.tpe.entity.concretes.user.User;
import com.tpe.entity.enums.RoleType;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.payload.mappers.UserMapper;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.payload.messages.SuccessMessages;
import com.tpe.payload.request.user.UserRequest;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.abstracts.BaseUserResponse;
import com.tpe.payload.response.user.UserResponse;
import com.tpe.repository.user.UserRepository;
import com.tpe.service.helper.PageableHelper;
import com.tpe.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor //UserRepository, UniquePropertyValidator, UserMapper, UserRoleService, ve PasswordEncoder sınıfları @RequiredArgsConstructor anotasyonu ile otomatik olarak enjekte edilir. Bu, tüm bağımlılıkları sınıfın yapıcısında sağlamak için kullanılır.
public class UserService {

    private final UserRepository userRepository;

    private final UniquePropertyValidator uniquePropertyValidator;

    private final UserMapper userMapper;

    private final UserRoleService userRoleService;

    private final PasswordEncoder passwordEncoder;

    private final PageableHelper pageableHelper;


    //DBdeki username,password,ssn,phonenumber,mailin unique olup olmadığının kontrolü
    public ResponseMessage<UserResponse> saveUser(UserRequest userRequest, String userRole) {

        uniquePropertyValidator.checkDuplicate(//DBdeki username,password,ssn,phonenumber,mailin unique olup olmadığının kontrolü., veri tutarlılığını sağlamak ve aynı bilgileri birden fazla kez girmeyi engellemek için kullanılır.
                userRequest.getUsername(),
                userRequest.getSsn(),
                userRequest.getPhoneNumber(),
                userRequest.getEmail()
        );



        //DTO-->POJO (kullanıcıdan alınan verilerin işlenebilir hale gelmesini sağlar.)
        User user =userMapper.mapUserRequestToUser(userRequest);

        //rol bilgisini setleme
        if(userRole.equalsIgnoreCase(RoleType.ADMIN.name())){
            if(Objects.equals(userRequest.getUsername(), "Admin")){ // userrequestten gelen kullanıcının, username bilgisi admine mi eşit.Eğer rol ADMIN ise ve kullanıcı adı "Admin" ise, built_in özelliği true olarak ayarlanır
                user.setBuilt_in(true);
            }
            user.setUserRole(userRoleService.getUserRole(RoleType.ADMIN));
        } else if (userRole.equalsIgnoreCase("Dean")) {
            user.setUserRole(userRoleService.getUserRole(RoleType.MANAGER)); // uygun RoleType değerleriyle ayarlanır
        } else if (userRole.equalsIgnoreCase("ViceDean")) {
            user.setUserRole(userRoleService.getUserRole(RoleType.ASSISTANT_MANAGER));
        } else {
            throw  new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_ROLE_MESSAGE, userRole));//Geçersiz bir rol girilmişse
        }


        //!!! password encode ediliyor
        user.setPassword(passwordEncoder.encode(user.getPassword())); // kullanıcı şifresi güvenli bir şekilde kodlanır. Şifreler bu şekilde depolanmalıdır, böylece düz metin olarak saklanmazlar.

        // !!! advisor degeri setleniyor( Admin-Manager ve AsstManager larin advisor olma ihtimali yok)
        user.setIsAdvisor(Boolean.FALSE);

        User savedUser = userRepository.save(user); //kullanıcı DBe kaydedilir.
        return ResponseMessage.<UserResponse>builder() //generic tipin builderı
                .message(SuccessMessages.USER_CREATED)
                .object(userMapper.mapUserToUserResponse(savedUser)) //pojoyu dtoya çevirme
                .build();
    }

    public Page<UserResponse> getUsersByPage(int page, int size, String sort, String type, String userRole) {

        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
        return userRepository.findByUserByRole(userRole, pageable).map(userMapper::mapUserToUserResponse); //pojoyu dtoya çeviriyoruz
    }



    public ResponseMessage<BaseUserResponse> getUserById(Long userId) {

        BaseUserResponse baseUserResponse;

        User user = userRepository.findById(userId).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE, userId)));

        // UserResponse --> Admin , Manager, Assistant_Manager
        // TeacherResponse --> Teacher (BaseUserResponsedan extends)
        // StudentResponse --> Student (BaseUserResponsedan extends)
        if(user.getUserRole().getRoleType() == RoleType.STUDENT){
            baseUserResponse = userMapper.mapUserToStudentResponse(user);
        } else if (user.getUserRole().getRoleType() == RoleType.TEACHER) {
            baseUserResponse = userMapper.mapUserToTeacherResponse(user);
        } else {
            baseUserResponse = userMapper.mapUserToUserResponse(user);
        }

        return ResponseMessage.<BaseUserResponse>builder()
                .message(SuccessMessages.USER_FOUND)
                .httpStatus(HttpStatus.OK)
                .object(baseUserResponse)
                .build();
    }









}
