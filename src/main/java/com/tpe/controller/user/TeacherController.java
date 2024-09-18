package com.tpe.controller.user;

import com.tpe.payload.request.user.TeacherRequest;
import com.tpe.payload.request.user.UserRequest;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.abstracts.BaseUserResponse;
import com.tpe.payload.response.user.StudentResponse;
import com.tpe.payload.response.user.TeacherResponse;
import com.tpe.payload.response.user.UserResponse;
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


    // !!! Bir rehber ogretmenin kendi ogrencilerinin tamamini getiren metod
    // Not: GetAllStudentByAdvisorUserName() *********************************************

    @GetMapping("/getAllStudentByAdvisorUsername") // http://localhost:8080/teacher/getAllStudentByAdvisorUsername  + GET
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public List<StudentResponse> getAllStudentByAdvisorUsername(HttpServletRequest request){ //danışman öğretmenin kullanıcı adı username başlığı aracılığıyla istekle birlikte sunucuya gönderiliyor. HttpServletRequest, bu başlığı okuyarak, öğretmenin kim olduğunu belirlememizi sağlar
        String userName = request.getHeader("username"); //okunabilirlik için değişkene atadık
        return teacherService.getAllStudentByAdvisorUsername(userName);
    }


    //öğretmen bilgilerini güncelleme (sadece belirli yetkideki userlar yapabilir)
    // Not: ODEVVV updateTeacherById() ***************************************************

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @PutMapping("/update/{userId}")  // http://localhost:8080/user/update/1
    //update işlemi olduğu için putmapping kullanıldı
    public ResponseMessage<TeacherResponse>updateTeacherForManagers(@RequestBody @Valid TeacherRequest teacherRequest,
                                                                    @PathVariable Long userId){
        //@RequestBody ile istek gövdesinde gönderilen JSON formatındaki TeacherRequest nesnesi alınır. Bu nesne, öğretmenin güncellenecek bilgilerini içerir (örneğin, adı, soyadı, branşı vb.).
        return teacherService.updateTeacherForManagers(teacherRequest,userId); //yöneticilerin kullanacağı bir method olduğu için password dahil edildi.
    }


    // belirli bir öğretmeni danışman öğretmen olarak atamak veya bu atamayı güncellemektir.
    // Not: ODEVV SaveAdvisorTeacherByTeacherId() bir teacherın idsiyle o teacherı advisor yapıyorsunuz****************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @PatchMapping("/saveAdvisorTeacher/{teacherId}") // http://localhost:8080/teacher/saveAdvisorTeacher/1
    //kısmi güncellenme olduğu için PatchMapping
    public ResponseMessage<UserResponse> savedAdvisorTeacher (@PathVariable Long teacherId){
        return teacherService.saveAdvisorTeacher(teacherId);
    }


    //belirli bir öğretmeni danışman öğretmen olarak sistemden silmek
    // Not : ODEVV  deleteAdvisorTeacherById() *******************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANTMANAGER')")
    @DeleteMapping("/deleteAdvisorTeacherById/{id}")// http://localhost:8080/teacher/deleteAdvisorTeacherById/1
    public ResponseMessage<UserResponse> deleteAdvisorTeacherById(@PathVariable Long id){
        return teacherService.deleteAdvisorTeacherById(id);
    }

    // Not :  getAllAdvisorTeacher() *****************************************************
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @GetMapping("/getAllAdvisorTeacher") // http://localhost:8080/teachers/getAllAdvisorTeacher/
    public List<UserResponse> getAllAdvisorTeacher(){
        return teacherService.getAllAdvisorTeacher();
}
}
