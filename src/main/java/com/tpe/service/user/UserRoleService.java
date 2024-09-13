package com.tpe.service.user;

import com.tpe.entity.concretes.user.UserRole;
import com.tpe.entity.enums.RoleType;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.repository.user.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRole getUserRole(RoleType roleType){
        return userRoleRepository.findByEnumRoleEquals(roleType).orElseThrow(()-> new ResourceNotFoundException(ErrorMessages.ROLE_NOT_FOUND));
        //UserRolede enum değişken olmadığı için, olmayan bir değişkenin ismini kullandıysak findByEnumRoleEquals bu türetilen bir method değildir. jpql,hql veya sql yazacağımız bir methoddur
    }
}
