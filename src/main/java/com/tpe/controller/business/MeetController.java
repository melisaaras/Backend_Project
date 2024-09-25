package com.tpe.controller.business;

import com.tpe.payload.request.business.MeetRequest;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.business.EducationTermResponse;
import com.tpe.payload.response.business.MeetResponse;
import com.tpe.service.business.MeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping ("/meet") //componnet anno. olan classta requestmapping yapılamaz çünkü bean oluşturacağı classta mapleme yapmaz.
@RequiredArgsConstructor
public class MeetController {

    private final MeetService meetService;


    // bir öğretmen (TEACHER yetkisine sahip kullanıcı) tarafından bir toplantıyı (Meet) kaydetmek için
    @PostMapping("/save") // http://localhost:8080/meet/save + POST + JSON
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseMessage<MeetResponse> saveMeet(HttpServletRequest httpServletRequest,
                                                  @RequestBody @Valid MeetRequest meetRequest){
        return meetService.saveMeet(httpServletRequest, meetRequest);
    }//httpServletRequest, özellikle kullanıcının kimliğini (username) alabilmek için kullanılır. Bu username ile daha sonra kullanıcının advisor teacher olup olmadığını kontrol etmek için gerekli bilgiler alınır.


    // Not: getAll *************************************************************
    @PreAuthorize("hasAnyAuthority( 'ADMIN')")
    @GetMapping("/getAll")  // http://localhost:8080/meet/getAll
    public List<MeetResponse> getAll(){
        return meetService.getAll();
    }

    // Not: getByMeetId ********************************************************
    @PreAuthorize("hasAnyAuthority( 'ADMIN')")
    @GetMapping("/getMeetById/{meetId}")  // http://localhost:8080/meet/getMeetById/1
    public ResponseMessage<MeetResponse> getMeetById(@PathVariable Long meetId){
        return meetService.getMeetById(meetId);
    }

    // Not: getAllWithPage *****************************************************
    @PreAuthorize("hasAnyAuthority( 'ADMIN')")
    @GetMapping("/getAllMeetByPage") // http://localhost:8080/meet/getAllMeetByPage?page=0&size=1
    public Page<MeetResponse> getAllMeetByPage(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ){
        return meetService.getAllMeetByPage(page,size);
    }

    // Not: gettAllByAdvTeacherByPage() ****************************************
    @PreAuthorize("hasAnyAuthority('TEACHER')")// http://localhost:8080/meet/getAllMeetByTeacher
    @GetMapping("/getAllMeetByAdvisorAsPage")
    public ResponseEntity<Page<MeetResponse>> getAllMeetByTeacher(
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ) {
        return meetService.getAllMeetByTeacher(httpServletRequest,page,size);
    }

    @DeleteMapping("/delete/{meetId}")// http://localhost:8080/meet/delete/2  + DELETE
    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    public ResponseMessage delete(@PathVariable Long meetId, HttpServletRequest httpServletRequest){
        return meetService.delete(meetId, httpServletRequest);
    }

    @GetMapping("/getAllMeetByStduent")// http://localhost:8080/meet/getAllMeetByStudent
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    public List<MeetResponse> getAllMeetByStudent(HttpServletRequest httpServletRequest){
        return meetService.getAllMeetByStudent(httpServletRequest);
    }

    @PutMapping("/update/{meetId}") // http://localhost:8080/meet/update/2  + PUT + JSON
    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    public ResponseMessage<MeetResponse> updateMeetById(@RequestBody @Valid MeetRequest meetRequest,
                                                        @PathVariable Long meetId,
                                                        HttpServletRequest httpServletRequest){
        return meetService.updateMeet(meetRequest, meetId, httpServletRequest);
    }
}
