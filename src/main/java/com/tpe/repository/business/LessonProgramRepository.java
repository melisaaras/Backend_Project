
package com.tpe.repository.business;

import com.tpe.entity.concretes.business.LessonProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public interface LessonProgramRepository extends JpaRepository<LessonProgram, Long> {
    List<LessonProgram> findByUsers_IdNull();
    List<LessonProgram> findByUsers_IdNotNull();

    @Query("select l from LessonProgram l inner join l.users users where users.username = ?1") //iki tablonun kesişimi için inner join
    Set<LessonProgram> getLessonProgramByUsersUsername(String userName);


}
