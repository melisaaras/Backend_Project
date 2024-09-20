
package com.tpe.controller.user;


import com.tpe.payload.request.user.StudentRequest;
import com.tpe.payload.request.user.StudentRequestWithoutPassword;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.user.StudentResponse;
import com.tpe.service.user.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @PreAuthorize("hasAnyAuthority('STUDENT')") //Bu, sadece 'STUDENT' yetkisine sahip kullanıcıların bu endpoint'i çağırabileceğini belirtir. Eğer kullanıcının bu otoritesi yoksa, yetkilendirme hatası (403 Forbidden) dönecektir.
    @PatchMapping ("/update") //// http://localhost:8080/student/updateStudent
    public ResponseEntity <String> updateStudent (@RequestBody @Valid StudentRequestWithoutPassword studentRequestWithoutPassword,
                                                  HttpServletRequest request){
        return studentService.updateStudent(studentRequestWithoutPassword, request);

    }


    // Not: updateStudent() **********************************************************
    // yöneticilerin öğrenciyi update etme işlemi, passwordü de güncelleyebilmeli

    @PutMapping("/update/{userId}")  // http://localhost:8080/student/update/2
@PreAuthorize("hasAnyAuthority ('ADMIN','MANAGER','ASSISTANT_MANAGER')")
        public ResponseMessage<StudentResponse> updateStudentForManagers(@PathVariable Long userId, @RequestBody @Valid StudentRequest studentRequest){

        return studentService.updateStudentForManagers(userId,studentRequest);
    }



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

