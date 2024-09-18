
package com.tpe.service.business;

import com.tpe.payload.request.business.LessonProgramRequest;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.business.LessonProgramResponse;
import com.tpe.repository.business.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessonProgramService {

    private final LessonRepository lessonRepository;

    public ResponseMessage<LessonProgramResponse> saveLessonProgram(LessonProgramRequest lessonProgramRequest) {
        return null;
    }
}
