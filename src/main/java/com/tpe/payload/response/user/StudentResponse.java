package com.tpe.payload.response.user;
import com.tpe.entity.concretes.business.LessonProgram;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpe.payload.response.abstracts.BaseUserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentResponse extends BaseUserResponse {

    //BaseUserResponse olmayıp studentta olması gereken fieldları yazıyoruz.

    private Set<LessonProgram> lessonProgramSet;
    private int studentNumber;
    private String motherName;
    private String fatherName;
    private boolean isActive;

}
