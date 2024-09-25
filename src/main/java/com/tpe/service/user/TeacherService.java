
package com.tpe.service.user;

import com.tpe.entity.concretes.business.LessonProgram;
import com.tpe.entity.concretes.user.User;
import com.tpe.entity.enums.RoleType;
import com.tpe.exception.ConflictException;
import com.tpe.payload.mappers.UserMapper;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.payload.messages.SuccessMessages;
import com.tpe.payload.request.user.TeacherRequest;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.user.StudentResponse;
import com.tpe.payload.response.user.TeacherResponse;
import com.tpe.payload.response.user.UserResponse;
import com.tpe.repository.user.UserRepository;
import com.tpe.service.business.LessonProgramService;
import com.tpe.service.helper.MethodHelper;
import com.tpe.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final UserRepository userRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final MethodHelper methodHelper;
    private final LessonProgramService lessonProgramService;



    public ResponseMessage<TeacherResponse> saveTeacher(TeacherRequest teacherRequest) {
        // !!! TODO : LessonProgram eklenecek
        Set<LessonProgram> lessonProgramSet =
                lessonProgramService.getLessonProgramById(teacherRequest.getLessonsIdList());

        uniquePropertyValidator.checkDuplicate(
                teacherRequest.getUsername(),
                teacherRequest.getSsn(),
                teacherRequest.getPhoneNumber(),
                teacherRequest.getEmail()
        );

        User teacher = userMapper.mapTeacherRequestToUser(teacherRequest);
        teacher.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));
        //!!! TODO : lessonProgram setlenecek

        teacher.setLessonProgramList(lessonProgramSet);
        teacher.setPassword(passwordEncoder.encode(teacherRequest.getPassword()));
        if(teacherRequest.getIsAdvisorTeacher()){
            teacher.setIsAdvisor(Boolean.TRUE);
        } else teacher.setIsAdvisor(Boolean.FALSE);

        User savedTeacher = userRepository.save(teacher);

        return ResponseMessage.<TeacherResponse>builder()
                .message(SuccessMessages.TEACHER_SAVE)
                .object(userMapper.mapUserToTeacherResponse(savedTeacher))
                .build();
    }



    // belirli bir öğretmenin (advisor) danışmanı olduğu öğrencilerin listesini döner.
    public List<StudentResponse> getAllStudentByAdvisorUsername(String userName) {

        User teacher = methodHelper.isUserExistByUsername(userName);//öğretmenin kullanıcı adını alır.Eğer öğretmen bulunursa, teacher adlı bir User nesnesine atanır. Bu nesne, öğretmenin bilgilerini içerir.
        methodHelper.checkAdvisor(teacher); //bulunan kullanıcının gerçekten bir danışman öğretmen olup olmadığını kontrol eder.kullanıcı danışman değilse, checkadvisorda bir hata fırlatır

        return userRepository.findByAdvisorTeacherId(teacher.getId())// danışman öğretmenin Id bilgisiyle ilişkili öğrenciler veritabanından getirilir.Burada teacher.getId() ile öğretmenin veritabanındaki benzersiz kimliği (ID) kullanılır.
                .stream()
                .map(userMapper::mapUserToStudentResponse)//userrepositorydan data çektiğimiz için pojo-->dto dönüşümü
                .collect(Collectors.toList());//Dönüştürülen veriler liste haline getirilir
    }


    // öğretmen bilgilerini güncellemek ve sonuç olarak güncellenmiş öğretmen bilgilerini istemciye döndürmektir.
    // Not: updateTeacher() **********************************************************
    public ResponseMessage<TeacherResponse> updateTeacherForManagers(TeacherRequest teacherRequest, Long userId) {
        User user = methodHelper.isUserExist(userId);
        // !!! Parametrede gelen id bir teacher a ait degilse exception firlatiliyor
        methodHelper.checkRole(user,RoleType.TEACHER); //sadece öğretmen bilgilerini güncellemeye yetkili kullanıcıların işlem yapmasını sağlar.

        //!!! TODO: LessonProgramlar getiriliyor
        Set<LessonProgram> lessonPrograms =
                lessonProgramService.getLessonProgramById(teacherRequest.getLessonsIdList());
        // !!! unique kontrolu
        uniquePropertyValidator.checkUniqueProperties(user, teacherRequest);//veri tutarlılığını ve bütünlüğünü sağlamaya yönelik bir önlem
        // !!! TeacherRequest DTO'sunu bir User POJO'suna dönüştürür.
        User updatedTeacher = userMapper.mapTeacherRequestToUpdatedUser(teacherRequest, userId);
        // !!! props. that does n't exist in mappers
        updatedTeacher.setPassword(passwordEncoder.encode(teacherRequest.getPassword())); //Şifreyi passwordEncoder ile şifreler ve updatedTeacher nesnesine atar. Bu, güvenlik amacıyla yapılır ve şifrelerin veritabanında düz metin olarak saklanmamasını sağlar.
        // !!! TODO: LessonProgram sonrasi eklenecek
        updatedTeacher.setLessonProgramList(lessonPrograms);
        updatedTeacher.setUserRole(userRoleService.getUserRole(RoleType.TEACHER)); //öğretmen rolünü güncellenmiş kullanıcıya atar

        User savedTeacher = userRepository.save(updatedTeacher); // veritabanındaki öğretmen kaydını günceller.

        return ResponseMessage.<TeacherResponse>builder()
                .object(userMapper.mapUserToTeacherResponse(savedTeacher))//Güncellenmiş öğretmen bilgilerini içeren TeacherResponse DTO'su.
                .message(SuccessMessages.TEACHER_UPDATE)// İşlemin başarılı olduğunu belirten bir başarı mesajı.
                .httpStatus(HttpStatus.OK) //HTTP yanıt kodu olarak 200 OK döndürülür.
                .build();
    }


    // Not: SaveAdvisorTeacher() ***********************************************************

    public ResponseMessage<UserResponse> saveAdvisorTeacher(Long teacherId) {
        // !!! Save de yazdigimiz ya varsa kontrolu
        User teacher = methodHelper.isUserExist(teacherId);

        // !!! id ile gelen user Teacher mi kontrolu
        methodHelper.checkRole(teacher,RoleType.TEACHER);

        // !!! id ile gelen teacher zaten advisor mi kontrolu ?
        if(Boolean.TRUE.equals(teacher.getIsAdvisor())) { // condition : teacher.getIsAdvisor()
            throw new ConflictException(
                    String.format(ErrorMessages.ALREADY_EXIST_ADVISOR_MESSAGE, teacherId));
        }

        //setadvisorla tureya çekerek advisor olarak kaydederiz.
        teacher.setIsAdvisor(Boolean.TRUE);
        userRepository.save(teacher);

        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.ADVISOR_TEACHER_SAVE)
                .object(userMapper.mapUserToUserResponse(teacher))
                .httpStatus(HttpStatus.OK)
                .build();


    }

    // Not : deleteAdvisorTeacherById() ********************************************************
    public ResponseMessage<UserResponse> deleteAdvisorTeacherById(Long teacherId) {

        User teacher = methodHelper.isUserExist(teacherId);
        // !!! id ile gelen user Teacher mi kontrolu
        methodHelper.checkRole(teacher,RoleType.TEACHER);

        // !!! id ile gelen teacheradvisor mi kontrolu ?
        methodHelper.checkAdvisor(teacher);

        //advisor teacherı false çektik. artık advisor teacher değil
        teacher.setIsAdvisor(Boolean.FALSE);
        userRepository.save(teacher);

        // !!! silinen advisor Teacherlarin Student lari varsa bu iliskinin de koparilmasi gerekiyor
        List<User> allStudents = userRepository.findByAdvisorTeacherId(teacherId); //, teacherId ile ilişkilendirilmiş tüm öğrencileri veritabanından alır.findByAdvisorTeacherId metodunun amacı, belirli bir öğretmeni danışman olarak atanmış tüm öğrencileri getirmektir.
        if(!allStudents.isEmpty()) {
            allStudents.forEach(students -> students.setAdvisorTeacherId(null));
        }//danışman öğretmene atanmış öğrenci varsa (!allStudents.isEmpty()), her öğrencinin advisorTeacherId özelliğini null olarak ayarlar. Bu, öğrencilerin danışman öğretmenle olan ilişkisinin kaldırılması anlamına gelir.
        //forEach Döngüsü: Öğrencilerin her birini döngüye alır ve setAdvisorTeacherId(null) çağrısı ile ilişkiyi temizler.

        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.ADVISOR_TEACHER_DELETE)
                .object(userMapper.mapUserToUserResponse(teacher))
                .httpStatus(HttpStatus.OK)
                .build();
    }

    // Not : getAllAdvisorTeacher() **************************************************************
    public List<UserResponse> getAllAdvisorTeacher() {

        return userRepository.findAllByAdvisor(Boolean.TRUE) // JPQL
                .stream()
                .map(userMapper::mapUserToUserResponse)//pojoyu dtoya çevirdik
                .collect(Collectors.toList());
    }
}
