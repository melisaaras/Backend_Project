package com.tpe.repository.user;

import com.tpe.entity.concretes.user.UserRole;
import com.tpe.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;


public interface UserRoleRepository extends JpaRepository<UserRole,Integer> {

    @Query("select r from UserRole r where r.roleType = ?1") //r.roleType = ?1, birden fazla parametre varsa
    // r.roleType =: roleType, tek parametreyse
        // aşağıdaki 1.parametrede gelene eşitse çalıştır demek
   Optional<UserRole>  findByEnumRoleEquals(RoleType roleType);





}
