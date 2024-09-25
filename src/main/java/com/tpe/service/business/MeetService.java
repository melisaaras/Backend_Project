
package com.tpe.service.business;

import com.tpe.entity.concretes.business.Meet;
import com.tpe.entity.concretes.user.User;
import com.tpe.entity.enums.RoleType;
import com.tpe.exception.BadRequestException;
import com.tpe.exception.ConflictException;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.payload.mappers.MeetMapper;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.payload.messages.SuccessMessages;
import com.tpe.payload.request.business.MeetRequest;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.business.MeetResponse;
import com.tpe.repository.business.MeetRepository;
import com.tpe.service.helper.MethodHelper;
import com.tpe.service.helper.PageableHelper;
import com.tpe.service.user.UserService;
import com.tpe.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service //business logic olduğunu belirtmek ve kodun okunurluluğunu artırmak için
@RequiredArgsConstructor
public class MeetService {

    private final MeetRepository meetRepository;
    private final UserService userService;
    private final MethodHelper methodHelper;
    private final DateTimeValidator dateTimeValidator;
    private final MeetMapper meetMapper;
    private PageableHelper pageableHelper;

    public ResponseMessage<MeetResponse> saveMeet(HttpServletRequest httpServletRequest, MeetRequest meetRequest) {

        //kimliği doğrulanmış kullanıcının kim olduğunu tespit etmek için
        String username = (String) httpServletRequest.getAttribute("username");

        User advisorTeacher = userService.getTeacherByUsername(username);

        //advisorteacher kontrolü
        methodHelper.checkAdvisor(advisorTeacher);

        //Toplantının başlangıç ve bitiş saatlerinin geçerli olup olmadığını kontrol ediyor.
        dateTimeValidator.checkTimeWithException(meetRequest.getStartTime(), meetRequest.getStopTime());//checkTimeWithException,starttime stoptimedan sonra mı eşit mi kontrolü

        //Öğretmenin daha önce oluşturduğu diğer toplantılarla çakışma olup olmadığını kontrol eder
        checkMeetConflict(advisorTeacher.getId(),
                meetRequest.getDate(),
                meetRequest.getStartTime(),
                meetRequest.getStopTime());

        for(Long studentId: meetRequest.getStudentIds()){ //MeetRequest içinde gelen öğrenci ID'lerinin (studentIds) her biri için döngü başlatılır.
            User student = methodHelper.isUserExist(studentId);//Her bir öğrenci ID'sine karşılık gelen öğrenci bilgisi (User) veritabanından bulunur. Eğer öğrenci mevcut değilse, hata dönebilir
            methodHelper.checkRole(student, RoleType.STUDENT);//Öğrencinin rolünün STUDENT olup olmadığını kontrol eder.

            checkMeetConflict(studentId,
                    meetRequest.getDate(),
                    meetRequest.getStartTime(),
                    meetRequest.getStopTime()); //Öğrencinin başka bir toplantısı olup olmadığını kontrol eder. Öğrencinin aynı tarih ve saatte başka bir toplantısı varsa, çakışma tespit edilir
        }

        List<User> students = userService.getStudentById(meetRequest.getStudentIds()); //Toplantıya katılacak öğrenciler sisteme ID’ler ile girildiğinden, bu ID’ler kullanılarak veritabanındaki öğrenci bilgileri (isim, roller, vs.) User nesneleri olarak sisteme alınır. Bu liste daha sonra toplantıya katılacak öğrencileri belirtmek için kullanılır.
        Meet meet = meetMapper.mepMeetRequestToMeet(meetRequest);//dto pojo dönüşümü
        meet.setStudentList(students); //Toplantıya katılacak öğrenci listesini (students) oluşturulan Meet nesnesine ekler.
        meet.setAdvisoryTeacher(advisorTeacher); //Toplantının danışman öğretmenini (advisorTeacher) oluşturulan Meet nesnesine atar.

        Meet savedMeet = meetRepository.save(meet);

        return ResponseMessage.<MeetResponse>builder()
                .message(SuccessMessages.MEET_SAVE) //Kaydedilen toplantı hakkında bir mesaj
                .object(meetMapper.mapMeetToMeetResponse(savedMeet)) //Kaydedilen toplantının detaylarını içeren bir nesne
                .httpStatus(HttpStatus.CREATED) //HTTP durumu olarak 201 Created
                .build();
    }

    //yardımcı method
    //meet çakışma kontrolü
    private void checkMeetConflict(Long userId, LocalDate date, LocalTime startTime, LocalTime stopTime){

        List<Meet> meets; //Kullanıcının daha önce planladığı veya katıldığı tüm toplantıları tutacak bir liste

        if(Boolean.TRUE.equals(userService.getUserByUserId(userId).getIsAdvisor())){ //Eğer kullanıcı danışman öğretmen ise, bu koşul true döner.

            meets = meetRepository.getByAdvisoryTeacher_IdEquals(userId);//Eğer kullanıcı danışman öğretmense, o öğretmenin sorumluluğunda olan tüm toplantıları meets listesine yükler.

        } else  meets = meetRepository.findByStudentList_IdEquals(userId);//Eğer kullanıcı bir öğrenci ise, bu öğrencinin katıldığı tüm toplantıları meets listesine ekler.

        for(Meet meet: meets){ // Kullanıcının tüm toplantılarını döngü ile tek tek kontrol eder.

            LocalTime existingStartTime = meet.getStartTime();
            LocalTime existingStopTime = meet.getStopTime(); // mevcut toplantının başlangıç ve bitiş saatlerini getirir.


            if(meet.getDate().equals(date) && //Yeni eklenen toplantının tarihi ile mevcut toplantıların tarihinin aynı olup olmadığını kontrol eder.Eğer tarihler aynıysa, saat bazında çakışma kontrolüne geçer.
                    (
                            (startTime.isAfter(existingStartTime) && startTime.isBefore(existingStopTime)) ||
                                    (stopTime.isAfter(existingStartTime) && stopTime.isBefore(existingStopTime)) ||
                                    (startTime.isBefore(existingStartTime) && stopTime.isAfter(existingStopTime)) ||
                                    (startTime.equals(existingStartTime) || stopTime.equals(existingStopTime))
                    )

            ) {
                throw new ConflictException(ErrorMessages.MEET_HOURS_CONFLICT);
            }
        }
    }

    public ResponseMessage delete(Long meetId, HttpServletRequest httpServletRequest) {
        Meet meet = isMeetExistById(meetId);
        isTeacherControl(meet, httpServletRequest);
        meetRepository.deleteById(meetId);

        return ResponseMessage.builder()
                .message(SuccessMessages.MEET_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();
    }
    private Meet isMeetExistById(Long meetId){
        return meetRepository.findById(meetId).orElseThrow(
                ()-> new ResourceNotFoundException(String.format(ErrorMessages.MEET_NOT_FOUND_MESSAGE, meetId))
        );
    }

    private void isTeacherControl(Meet meet, HttpServletRequest httpServletRequest){
        String userName = (String) httpServletRequest.getAttribute("username");
        User teacher = methodHelper.isUserExistByUsername(userName);
        if(
                (teacher.getUserRole().getRoleType().equals(RoleType.TEACHER)) &&
                        !(meet.getAdvisoryTeacher().getId().equals(teacher.getId()))
        )
        {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }
    }

    public List<MeetResponse> getAllMeetByStudent(HttpServletRequest httpServletRequest) {

        String userName = (String) httpServletRequest.getAttribute("username");
        User student = methodHelper.isUserExistByUsername(userName);
        methodHelper.checkRole(student, RoleType.STUDENT);

        return meetRepository.findByStudentList_IdEquals(student.getId())
                .stream()
                .map(meetMapper::mapMeetToMeetResponse)
                .collect(Collectors.toList());
    }

    public List<MeetResponse>getAll(){
        return meetRepository.findAll()
                .stream()
                .map(meetMapper::mapMeetToMeetResponse)
                .collect(Collectors.toList());
    }

    public ResponseMessage<MeetResponse>getMeetById(Long meetId){
        return ResponseMessage.<MeetResponse>builder()
                .message(SuccessMessages.MEET_FOUND)
                .httpStatus(HttpStatus.OK)
                .object(meetMapper.mapMeetToMeetResponse(isMeetExistById(meetId)))
                .build();
    }

    public ResponseEntity<Page<MeetResponse>> getAllMeetByTeacher(HttpServletRequest httpServletRequest,
                                                                  int page,
                                                                  int size){
        String userName = (String) httpServletRequest.getAttribute("username");
        User advisoryTeacher = userService.getTeacherByUsername(userName);
        methodHelper.checkAdvisor(advisoryTeacher);

        Pageable pageable = pageableHelper.getPageableWithProperties(page,size);
        return ResponseEntity.ok(meetRepository.findByAdvisoryTeacher_IdEquals(advisoryTeacher.getId(), pageable)
                .map(meetMapper::mapMeetToMeetResponse));
    }

    public Page<MeetResponse> getAllMeetByPage(int page, int size){
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size);
        return meetRepository.findAll(pageable).map(meetMapper::mapMeetToMeetResponse);
    }


    public ResponseMessage<MeetResponse> updateMeet(MeetRequest meetRequest, Long meetId, HttpServletRequest httpServletRequest) {

        Meet meet = isMeetExistById(meetId);
        isTeacherControl(meet, httpServletRequest);
        dateTimeValidator.checkTimeWithException(meetRequest.getStartTime(),
                meetRequest.getStopTime());

        if(
                !(meet.getDate().equals(meetRequest.getDate()) &&
                        meet.getStartTime().equals(meetRequest.getStartTime()) &&
                        meet.getStopTime().equals(meetRequest.getStopTime())
                )
        ){
            // !!! sudent icin cakisma kontrolu
            for(Long studentId: meetRequest.getStudentIds()){
                checkMeetConflict(studentId,
                        meetRequest.getDate(),
                        meetRequest.getStartTime(),
                        meetRequest.getStopTime());
            }
            // !!! teacher icin cakisma kontrolu
            checkMeetConflict(meet.getAdvisoryTeacher().getId(),
                    meetRequest.getDate(),
                    meetRequest.getStartTime(),
                    meetRequest.getStopTime());
        }

        List<User> students = userService.getStudentById(meetRequest.getStudentIds());
        Meet updatedMeet =  meetMapper.mapMeetUpdatedRequestToMeet(meetRequest, meetId);
        updatedMeet.setStudentList(students);
        updatedMeet.setAdvisoryTeacher(meet.getAdvisoryTeacher());

        Meet savedMeet = meetRepository.save(updatedMeet);

        return ResponseMessage.<MeetResponse>builder()
                .message(SuccessMessages.MEET_UPDATE)
                .httpStatus(HttpStatus.OK)
                .object(meetMapper.mapMeetToMeetResponse(savedMeet))
                .build();

    }
}







