
package com.tpe.service.user;

import com.tpe.entity.concretes.user.User;
import com.tpe.entity.enums.RoleType;
import com.tpe.payload.mappers.UserMapper;
import com.tpe.payload.messages.SuccessMessages;
import com.tpe.payload.request.user.StudentRequest;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.user.StudentResponse;
import com.tpe.repository.user.UserRepository;
import com.tpe.service.helper.MethodHelper;
import com.tpe.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final MethodHelper methodHelper; //isuserexist methodları için
    private final UniquePropertyValidator uniquePropertyValidator; //fieldların uniquelik kontrolü için
    private UserMapper userMapper; //dto pojo dönüşümü için
    private final PasswordEncoder passwordEncoder; //passwordü hashlemek için
    private UserRoleService userRoleService;


    public ResponseMessage<StudentResponse> saveStudent(StudentRequest studentRequest) {

        User advisorTeacher = methodHelper.isUserExist(studentRequest.getAdvisorTeacherId());
        methodHelper.checkAdvisor(advisorTeacher);
        uniquePropertyValidator.checkDuplicate(
                studentRequest.getUsername(),
                studentRequest.getSsn(),
                studentRequest.getPhoneNumber(),
                studentRequest.getEmail()
        );
        User student = userMapper.mapStudentRequestToUser(studentRequest);
        student.setAdvisorTeacherId(advisorTeacher.getId());
        student.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
        student.setUserRole(userRoleService.getUserRole(RoleType.STUDENT));
        student.setActive(true);
        student.setIsAdvisor(Boolean.FALSE);
        student.setStudentNumber(getLastNumber());

        return ResponseMessage.<StudentResponse>builder()
                .object(userMapper.mapUserToStudentResponse(userRepository.save(student)))
                .message(SuccessMessages.STUDENT_SAVE)
                .build();
        //methodu tanımlarken parantez içindeki  data type ve değişken ismi parametredir.
        //methodu kullanırken parantez içindeki argümandır
    }

    private int getLastNumber(){
        if (userRepository.findStudent(RoleType.STUDENT)){ //öğrenci yoksa 1000 döndürecek
            return 1000;
        }
        return userRepository.getMaxStudentNumber() + 1 ;
    }


    public ResponseMessage changeStatusOfStudent(Long studentId, boolean status) {

        User student = methodHelper.isUserExist(studentId);
        methodHelper.checkRole(student, RoleType.STUDENT);
        student.setActive(status);
        userRepository.save(student);

        return ResponseMessage.builder()
                .message("Student is " + (status ? "active" : "passive"))
                .httpStatus(HttpStatus.OK)
                .build();
    }
    }


