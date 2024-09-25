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
import com.tpe.payload.request.business.UpdateStudentInfoRequest;
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
import org.springframework.http.ResponseEntity;
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

        // Kullanıcı Kimliğini Doğrulama (HTTP Servlet Request) HTTP isteğinin kim tarafından yapıldığını öğrenmek için
        //HttpServletRequest nesnesinden alınan username özelliği ile isteği yapan öğretmenin kullanıcı adı elde edilir. Bu, öğretmen doğrulaması ve ilerleyen adımlarda öğretmen bilgilerini kaydetmek için gereklidir.
       String teacherUsername = (String) httpServletRequest.getAttribute("username");

       //öğrenci var mı kontrolü.Eğer öğrenci yoksa bir hata fırlatılır.
        User student = methodHelper.isUserExist(studentInfoRequest.getStudentId());

        //Veritabanında bulunan kullanıcının öğrenci rolüne sahip olup olmadığını kontrol etmek.
        methodHelper.checkRole(student, RoleType.STUDENT);

        // HTTP isteğini yapan öğretmeni bulmak.
        User teacher = userService.getTeacherByUsername(teacherUsername);

        //Öğrencinin katıldığı dersin olup olmadığını kontrol etmek.
        Lesson lesson = lessonService.isLessonExistById(studentInfoRequest.getLessonId());

        //educationtermi getirelim
        EducationTerm educationTerm = educationTermService.getEducationTermById(studentInfoRequest.getEducationTermId());


        //Aynı öğrenci için aynı ders adına ait bir studentInfo kaydı olup olmadığına bakılır. Eğer varsa, bir hata fırlatılır. Bu, aynı dersin birden fazla kez kaydedilmesini engeller.
        checkSameLesson(studentInfoRequest.getStudentId(), lesson.getLessonName());


        //Not Ortalamasını Hesaplama ve Harf Notunu Belirleme
        Note note = checkLetterGrade(calculateExamAverage(studentInfoRequest.getMidtermExam(), studentInfoRequest.getFinalExam()));


        //İstekten gelen verilerle yeni bir öğrenci not bilgisi (StudentInfo) nesnesi oluşturmak
        //studentInfoRequest'ten gelen sınav sonuçları, not ortalaması ve harf notu ile bir StudentInfo nesnesi oluşturulur. Bu nesne ilerleyen adımlarda veritabanına kaydedilecektir.
        StudentInfo studentInfo = studentInfoMapper.mapStudentInfoRequestToStudentInfo(
                studentInfoRequest,
                note,
                calculateExamAverage(studentInfoRequest.getMidtermExam(), studentInfoRequest.getFinalExam()));


        //Daha önce bulunmuş olan student, teacher, educationTerm ve lesson nesneleri, studentInfo nesnesine eklenir. Bu sayede, öğrenci bilgileri eksiksiz olarak kaydedilir.
        studentInfo.setStudent(student);
        studentInfo.setTeacher(teacher);
        studentInfo.setEducationTerm(educationTerm);
        studentInfo.setLesson(lesson);


        //oluşturulan studentInfo veritabanına kaydedilir
        StudentInfo savedStudentInfo = studentInfoRepository.save(studentInfo);


        //Kaydedilen öğrenci not bilgilerini ve başarı mesajını istemciye geri döndürmek.
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


    //bir öğrencinin studentinfosunu silmek için
    public ResponseMessage deleteById(Long studentInfoId) {


        //Öğrenci Not Bilgisinin Var Olup Olmadığını Kontrol Etme
        isStudentInfoExistById(studentInfoId);


        //studentInfoId ile ilişkilendirilen öğrenci not bilgisi kaydını veritabanından silmek
        studentInfoRepository.deleteById(studentInfoId);


        //Silme işleminin başarılı olduğunu belirten bir yanıt mesajını istemciye geri döndürmek
        return ResponseMessage.builder()
                .message(SuccessMessages.STUDENT_INFO_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();
    }


    public StudentInfo isStudentInfoExistById(Long id){
        return studentInfoRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.STUDENT_INFO_NOT_FOUND, id)));
    }


    //öğrenci not bilgilerini sayfalama ve sıralama özellikleriyle birlikte almak için
    public Page<StudentInfoResponse> getAllStudentInfoByPage(int page, int size, String sort, String type) {

        //oluşturulan pageable nesnesi ile öğrenci not bilgilerini sayfalama ve sıralama yaparak almak.
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);


        return studentInfoRepository.findAll(pageable)
                .map(studentInfoMapper::mapStudentInfoToStudentInfoResponse);
    }


    public ResponseMessage<StudentInfoResponse> update(UpdateStudentInfoRequest studentInfoRequest, Long studentInfoId) {

        //lesson var mı kontrolü
        Lesson lesson = lessonService.isLessonExistById(studentInfoRequest.getLessonId());

        //educationterm var mı kontrolü
        EducationTerm educationTerm = educationTermService.getEducationTermById(studentInfoRequest.getEducationTermId());

        //studentinfo var mı kontrolü
       StudentInfo studentInfo = isStudentInfoExistById(studentInfoId);

       //studentInfoRequestten gelen lessonların not ortalaması
       Double noteAverage = calculateExamAverage(studentInfoRequest.getMidtermExam(),studentInfoRequest.getFinalExam());

       //notların alfabetik karşılığı
       Note note = checkLetterGrade(noteAverage);

        //dto-pojo dönüşümü
       StudentInfo studentInfoForUpdate = studentInfoMapper.mapUpdateStudentInfoRequestToStudentInfo(studentInfoRequest,studentInfoId,lesson,educationTerm,note,noteAverage);

       //studentinfo nesnemi dönüşümden gelen nesneye setliyoruz
        studentInfoForUpdate.setStudent(studentInfo.getStudent());

        studentInfoForUpdate.setTeacher(studentInfo.getTeacher());

        //repoya save
        StudentInfo updatedStudentInfo = studentInfoRepository.save(studentInfoForUpdate);

        return ResponseMessage.<StudentInfoResponse>builder()
                .message(SuccessMessages.STUDENT_INFO_UPDATE)
                .httpStatus(HttpStatus.OK)
                .object(studentInfoMapper.mapStudentInfoToStudentInfoResponse(updatedStudentInfo))//pojoyu dtoya dönüştürme
                .build();

    }



    public Page<StudentInfoResponse> getAllForTeacher(HttpServletRequest httpServletRequest, int page, int size) {

        //iki parametreli pageable nesne oluşturduk
        Pageable pageable = pageableHelper.getPageableWithProperties(page,size);

        //requesti gönderen teacherın unique bilgisini elde ederek kim olduğunu görürüz. http servlet
        String username = (String) httpServletRequest.getAttribute("username");


        return studentInfoRepository
                .findByTeacherId_UsernameEquals(username, pageable)
                .map(studentInfoMapper::mapStudentInfoToStudentInfoResponse);
    }



    public Page<StudentInfoResponse> getAllForStudent(HttpServletRequest httpServletRequest, int page, int size) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page,size);

        String username = (String) httpServletRequest.getAttribute("username");

        return studentInfoRepository
                .findByStudentId_UsernameEquals(username, pageable)
                .map(studentInfoMapper::mapStudentInfoToStudentInfoResponse);
    }

}
