package com.tpe.controller.user;

import com.tpe.payload.request.user.TeacherRequest;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.user.StudentResponse;
import com.tpe.payload.response.user.TeacherResponse;
import com.tpe.service.user.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping("/save") // http://localhost:8080/teacher/save + POST + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<TeacherResponse>> saveTeacher(
            @RequestBody @Valid TeacherRequest teacherRequest){
        return ResponseEntity.ok(teacherService.saveTeacher(teacherRequest));
    }

    // Not: GetAllStudentByAdvisorUserName() *********************************************
    // !!! Bir rehber ogretmenin kendi ogrencilerinin tamamini getiren metod
    @GetMapping("/getAllStudentByAdvisorUsername") // http://localhost:8080/teacher/getAllStudentByAdvisorUsername  + GET
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public List<StudentResponse> getAllStudentByAdvisorUsername(HttpServletRequest request){
        String userName = request.getHeader("username");
        return teacherService.getAllStudentByAdvisorUsername(userName);
    }


    // Not: ODEVVV updateTeacherById() ***************************************************

    // Not: ODEVV SaveAdvisorTeacherByTeacherId() bir teacherın idsiyle o teacherı advisor yapıyorsunuz****************************************

    // Not : ODEVV  deleteAdvisorTeacherById() *******************************************

    // Not :  getAllAdvisorTeacher() *****************************************************
}
