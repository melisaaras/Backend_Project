
package com.tpe.controller.business;

import com.tpe.payload.request.business.LessonProgramRequest;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.business.LessonProgramResponse;
import com.tpe.payload.response.user.UserResponse;
import com.tpe.service.business.LessonProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/lessonPrograms")
@RequiredArgsConstructor
public class LessonProgramController {

    private final LessonProgramService lessonProgramService;

    @PostMapping("/save") // http://localhost:8080/lessonPrograms/save
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<LessonProgramResponse> saveLessonProgram(@RequestBody @Valid LessonProgramRequest lessonProgramRequest){
        return lessonProgramService.saveLessonProgram(lessonProgramRequest);
    }


    @GetMapping("/getAll") // http://localhost:8080/lessonPrograms/getAll
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public List<LessonProgramResponse> getAllLessonPrograms(){
        return lessonProgramService.getAllLessonPrograms();
    }

    @GetMapping("/getById/{id}")// http://localhost:8080/lessonPrograms/getById/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public LessonProgramResponse getLessonProgramById(@PathVariable Long id){
        return lessonProgramService.getLessonProgramById(id);
    }

    //herhangi bir ataması yapılmamış dersler
    @GetMapping("/getAllUnassigned") // http://localhost:8080/lessonPrograms/getAllUnassigned
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllUnassigned(){
        return lessonProgramService.getAllUnassigned();
    }

    // Not : ODEVV getAllLessonProgramAssigned() **************************************************
    @GetMapping("/getAllAssigned") //http://localhost:8080/lessonPrograms/getAllAssigned
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllAssigned(){
        return lessonProgramService.getAllAssigned();
    }


    @DeleteMapping("/delete/{id}")// http://localhost:8080/lessonPrograms/delete/2
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage deleteById(@PathVariable Long id){
        return lessonProgramService.deleteById(id);
    }



    // Not : ( ODEV ) getAllWithPage() ***********************************************************
    @GetMapping("/getAllLessonProgramByPage") // http://localhost:8080/lessonPrograms/getAllLessonProgramByPage?page=0&size=1&sort=id&type=desc
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public Page<LessonProgramResponse> getAllLessonProgramByPage (
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type){
        return lessonProgramService.getAllLessonProgramByPage(page,size,sort,type);
    }


    //TEACHERIN LESSON PROGRAMINI GETİRMESİ
    @GetMapping("/getAllLessonProgramByTeacher")// http://localhost:8080/lessonPrograms/getAllLessonProgramByTeacher
    @PreAuthorize("hasAnyAuthority('TEACHER','STUDENT')")
    public Set<LessonProgramResponse> getAllByTeacher(HttpServletRequest httpServletRequest){
        return lessonProgramService.getAllLessonProgramByUser(httpServletRequest);
    }



    // Not : getLessonProgramByStudent() ********************************************************
    // bir Ogrenci kendine ait lessonProgramlari getiriyor
    @GetMapping("/getAllLessonProgramByStudent") // http://localhost:8080/lessonPrograms/getAllLessonProgramByStudent
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    public Set<LessonProgramResponse> getAllByStudent(HttpServletRequest httpServletRequest){
        return lessonProgramService.getAllLessonProgramByUser(httpServletRequest);
    }



}

