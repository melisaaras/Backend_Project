package com.tpe.service.business;


import com.tpe.entity.concretes.business.EducationTerm;
import com.tpe.entity.concretes.business.Lesson;
import com.tpe.entity.concretes.business.StudentInfo;
import com.tpe.entity.concretes.user.User;
import com.tpe.entity.enums.Note;
import com.tpe.entity.enums.RoleType;
import com.tpe.exception.ConflictException;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.payload.mappers.StudentInfoMapper;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.payload.messages.SuccessMessages;
import com.tpe.payload.request.business.StudentInfoRequest;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.business.StudentInfoResponse;
import com.tpe.repository.business.StudentInfoRepository;
import com.tpe.service.helper.MethodHelper;
import com.tpe.service.helper.PageableHelper;
import com.tpe.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class StudentInfoService {

    private final StudentInfoRepository studentInfoRepository;

    private final MethodHelper methodHelper;

    private final UserService userService;

    private final LessonService lessonService;

    private final EducationTermService educationTermService;

    private final StudentInfoMapper studentInfoMapper;

    private final PageableHelper pageableHelper;



    //application.propertiesdan aldığımız değişkenler
    @Value("${midterm.exam.impact.percentage}")
    private Double midtermExamPercentage;
    @Value("${final.exam.impact.percentage}")
    private Double finalExamPercentage;


    //studentinfoyu save etme
    public ResponseMessage<StudentInfoResponse> saveStudentInfo(HttpServletRequest httpServletRequest, StudentInfoRequest studentInfoRequest) {

        //htppservletrequest kullandığımız için requesti kim yaptı kontrolü
       String teacherUsername = (String) httpServletRequest.getAttribute("username");

       //öğrenci var mı kontrolü
        User student = methodHelper.isUserExist(studentInfoRequest.getStudentId());

        //rol kontrolü
        methodHelper.checkRole(student, RoleType.STUDENT);

        //teacher
        User teacher = userService.getTeacherByUsername(teacherUsername);

        //lessonı getirelim
        Lesson lesson = lessonService.isLessonExistById(studentInfoRequest.getLessonId());

        //educationtermi getirelim
        EducationTerm educationTerm = educationTermService.getEducationTermById(studentInfoRequest.getEducationTermId());


        //ilgili öğrencinin aynı ders isminde studentinfosu var mı kontrolü
        checkSameLesson(studentInfoRequest.getStudentId(), lesson.getLessonName());

        Note note = checkLetterGrade(calculateExamAverage(studentInfoRequest.getMidtermExam(), studentInfoRequest.getFinalExam()));

        StudentInfo studentInfo = studentInfoMapper.mapStudentInfoRequestToStudentInfo(
                studentInfoRequest,
                note,
                calculateExamAverage(studentInfoRequest.getMidtermExam(), studentInfoRequest.getFinalExam()));

        studentInfo.setStudent(student);
        studentInfo.setTeacher(teacher);
        studentInfo.setEducationTerm(educationTerm);
        studentInfo.setLesson(lesson);

        StudentInfo savedStudentInfo = studentInfoRepository.save(studentInfo);

        return ResponseMessage.<StudentInfoResponse>builder()
                .message(SuccessMessages.STUDENT_INFO_SAVE)
                .httpStatus(HttpStatus.CREATED)
                .object(studentInfoMapper.mapStudentInfoToStudentInfoResponse(savedStudentInfo))
                .build();
    }


    //yardımcı method
    //ilgili öğrencinin aynı ders isminde studentinfosu var mı kontrolü
    private void checkSameLesson(Long studentId, String lessonName){
        boolean isLessonDuplicationExist = studentInfoRepository.getAllByStudentId_Id(studentId) //melisaya ait olan tüm infoları getiren query
                .stream()
                .anyMatch(s->s.getLesson().getLessonName()//studentın tüm infoları içerisinde lessonNamee ait olan infoyu getirelim
                 .equalsIgnoreCase(lessonName));

        if (isLessonDuplicationExist){
            throw new ConflictException(String.format(ErrorMessages.LESSON_ALREADY_EXIST_WITH_LESSON_NAME,lessonName));
        }

    }


    //yardımcı method
    //averagei hesaplayacak
    private Double calculateExamAverage(Double midtermExam, Double finalExam){
        return ((midtermExamPercentage * midtermExam) + (finalExamPercentage * finalExam));
    }


    //yardımcı method
    //öğrencinin average puanını setleyip harf olarak döndürecek
    private Note checkLetterGrade(Double average){
        if(average<50.00){
            return Note.FF;
        } else if (average<60) {
            return Note.DD;
        } else if(average<65){
            return Note.CC;
        } else if (average<70){
            return Note.CB;
        } else if (average<75){
            return Note.BB;
        } else if (average<80){
            return Note.BA;
        } else {
            return Note.AA;
        }
    }

    public ResponseMessage deleteById(Long studentInfoId) {
        isStudentInfoExistById(studentInfoId);
        studentInfoRepository.deleteById(studentInfoId);
        return ResponseMessage.builder()
                .message(SuccessMessages.STUDENT_INFO_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public StudentInfo isStudentInfoExistById(Long id){
        return studentInfoRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.STUDENT_INFO_NOT_FOUND, id)));
    }

    public Page<StudentInfoResponse> getAllStudentInfoByPage(int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
        return studentInfoRepository.findAll(pageable)
                .map(studentInfoMapper::mapStudentInfoToStudentInfoResponse);
    }







}
