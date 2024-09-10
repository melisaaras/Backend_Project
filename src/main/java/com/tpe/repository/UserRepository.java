package com.tpe.repository;

import com.tpe.entity.concretes.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

//3.ADIM:


public interface UserRepository extends JpaRepository<User,Long> {

    //4.ADIM:
    User findByUsernameEquals(String username);
}
