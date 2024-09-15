package com.tpe.service.helper;


import com.tpe.entity.concretes.user.User;
import com.tpe.entity.enums.RoleType;
import com.tpe.exception.BadRequestException;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MethodHelper {

    private final UserRepository userRepository;

    // !!! isUserExist
    public User isUserExist(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE,
                        userId)));
    }

    public void checkBuiltIn(User user){
        if(Boolean.TRUE.equals(user.getBuilt_in())) {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }
    }

    public User isUserExistByUsername(String username){
        User user = userRepository.findByUsernameEquals(username);

        if(user.getId() == null){
            throw new ResourceNotFoundException(ErrorMessages.NOT_FOUND_USER_MESSAGE);
        }
        return user;
    }

    public void checkAdvisor(User user){
        if(Boolean.FALSE.equals(user.getIsAdvisor())){
            throw  new BadRequestException(String.format(ErrorMessages.NOT_FOUND_ADVISOR_MESSAGE, user.getId()));
        }
    }

    // !!! Rol kontrolu yapan method.
    //dbden gelen rol bilgisi ve requestten gelen rol bilgisi eşit mi kontrolü
    public void checkRole(User user, RoleType roleType){
        if (!user.getUserRole().getRoleType().equals(roleType)) {
            throw new ResourceNotFoundException(
                    String.format(ErrorMessages.NOT_FOUND_USER_WITH_ROLE_MESSAGE, user.getId(),roleType));
        }
    }
}