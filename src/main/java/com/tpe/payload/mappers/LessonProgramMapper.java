package com.tpe.payload.mappers;
import com.tpe.entity.concretes.business.EducationTerm;
import com.tpe.entity.concretes.business.Lesson;
import com.tpe.entity.concretes.business.LessonProgram;
import com.tpe.payload.request.business.LessonProgramRequest;
import com.tpe.payload.response.business.LessonProgramResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Set;
@Component
@RequiredArgsConstructor
public class LessonProgramMapper {




    //dto-->pojo dönüşümü
    public LessonProgram mapLessonProgramRequestToLessonProgram(LessonProgramRequest lessonProgramRequest,
                                                                    Set<Lesson> lessonSet, EducationTerm educationTerm){
        return LessonProgram.builder()
                .startTime(lessonProgramRequest.getStartTime())
                .stopTime(lessonProgramRequest.getStopTime())
                .day(lessonProgramRequest.getDay())
                .lessons(lessonSet) //requestte değişken olarak lessonid var bunu lessonprogramdaki lessona çevirdik.
                .educationTerm(educationTerm)
                .build();
    }



    //pojo-->DTO dönüşümü
    public LessonProgramResponse mapLessonProgramToLessonProgramResponse(LessonProgram  lessonProgram){
        return LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .lessonProgramId(lessonProgram.getId())
                .lessonName(lessonProgram.getLessons())
                .build();
    }





}
