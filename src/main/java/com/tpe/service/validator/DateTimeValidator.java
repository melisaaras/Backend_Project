package com.tpe.service.validator;
import com.tpe.exception.BadRequestException;
import com.tpe.payload.messages.ErrorMessages;
import org.springframework.stereotype.Component;
import java.time.LocalTime;
@Component
public class DateTimeValidator {


    //starttime stoptimedan sonra mı eşit mi kontrolü
    private boolean checkTime(LocalTime start, LocalTime stop){
        return start.isAfter(stop) || start.equals(stop);
    }


    //yukardaki method true dönerse bu methodla da exception fırlatma
    //çalışmasını istediğim method bu olduğu için yukardakini private bunu public yaptık ve o methodu buraya gömdük
    public void checkTimeWithException(LocalTime start, LocalTime stop){
        if(checkTime(start, stop)){
            throw new BadRequestException(ErrorMessages.TIME_NOT_VALID_MESSAGE);
        }
    }
}
