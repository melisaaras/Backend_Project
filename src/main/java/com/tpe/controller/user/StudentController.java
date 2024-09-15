
package com.tpe.controller.user;

import com.tpe.payload.request.user.StudentRequest;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.user.StudentResponse;
import com.tpe.service.user.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/save") // http://localhost:8080/student/save + POST + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<StudentResponse>> saveStudent(@RequestBody @Valid StudentRequest studentRequest) {
        return ResponseEntity.ok(studentService.saveStudent(studentRequest));
    }


    // Not: updateStudentForStudents() ***********************************************
    // !!! ogrencinin kendisini update etme islemi, passwordü olmayan bir dto classı oluşturalacak

    //biri baseuser (pasword)  ve  diğeri abstract userdan extend

    // Not: updateStudent() **********************************************************
    // yöneticilerin öğrenciyi update etme işlemi, password
    //TODO: LessonProgram ekleem metodu yazilacak


    //pasif veya aktiflik
    @GetMapping("/changeStatus")  // http://localhost:8080/student/changeStatus?id=1&status=true + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage changeStatusOfStudent(@RequestParam Long id, @RequestParam boolean status){ //öğrenci idsi ve status bilgisi alınmalı
        //status değeri true ise, bu idli studentın status değerini trueya çekmiş oluruz.
        //status değeri false ise, bu idli studentın status değerini falsea çekmiş oluruz.

        return studentService.changeStatusOfStudent(id, status);
    }

}

