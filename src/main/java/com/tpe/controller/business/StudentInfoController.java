package com.tpe.controller.business;


import com.tpe.payload.request.business.StudentInfoRequest;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.business.StudentInfoResponse;
import com.tpe.service.business.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/studentInfo")
@RequiredArgsConstructor
public class StudentInfoController {

    private final StudentInfoService studentInfoService;

    //ip, bina; port daire nodur

    //studentinfoyu save etme
    @PostMapping("/save") // http://localhost:8080/studentInfo/save + POST
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseMessage<StudentInfoResponse> saveStudentInfo(HttpServletRequest httpServletRequest, @RequestBody @Valid StudentInfoRequest studentInfoRequest ){

        return studentInfoService.saveStudentInfo(httpServletRequest,studentInfoRequest);
    }


    @DeleteMapping("/delete/{studentInfoId}")  // http://localhost:8080/studentInfo/delete/2  + DELETE
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseMessage deleteById(@PathVariable Long studentInfoId){
        return studentInfoService.deleteById(studentInfoId);
    }



    @GetMapping("/getAllStudentInfoByPage") // http://localhost:8080/studentInfo/getAllStudentInfoByPage?page=0&size=10&sort=id&type=desc + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Page<StudentInfoResponse> getAllStudentInfoByPage(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type
    ){
        return studentInfoService.getAllStudentInfoByPage(page,size,sort,type);
    }



}
