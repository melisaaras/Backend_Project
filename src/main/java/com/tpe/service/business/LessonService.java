package com.tpe.service.business;

import com.tpe.entity.concretes.business.Lesson;
import com.tpe.exception.ConflictException;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.payload.mappers.LessonMapper;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.payload.messages.SuccessMessages;
import com.tpe.payload.request.business.LessonRequest;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.business.LessonResponse;
import com.tpe.repository.business.LessonRepository;
import com.tpe.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {


    private final LessonRepository lessonRepository;

    private final LessonMapper lessonMapper; //injection

    private final PageableHelper pageableHelper;



    public ResponseMessage<LessonResponse> saveLesson(LessonRequest lessonRequest) {

        //methodun içinde öncelikle kontrol etmme gereken bir şey var mı diye anlamak çin parametrede gelen classa bakmalıyız. mesela bu method içinde aynı isimde iki ders eklenmemesini kontrol etmeliyiz. bu kontrolü update methdounda da kullanabileceğimiz için aşağıda scop dışında isLessonExistByLessonName adında bir method yazarız.

        isLessonExistByLessonName(lessonRequest.getLessonName()); //lessonrequestten lessonnamei getir

        Lesson savedLesson = lessonRepository.save(lessonMapper.mapLessonRequestToLesson(lessonRequest));//dersi DBe kaydedeceğimiz için dtoyu pojoya çevirerek

        return ResponseMessage.<LessonResponse> builder()
                .object(lessonMapper.mapLessonToLessonResponse(savedLesson))//clienta returnleyeceğimiz için pojoyu dtoya çevirdik
                .message(SuccessMessages.LESSON_SAVE)
                .httpStatus(HttpStatus.CREATED)
                .build();


    }

    //aynı isimde ders var mı yok mu kontrolü yapan yardımcı method
    private boolean isLessonExistByLessonName(String lessonName){

        boolean lessonExist = lessonRepository.existsLessonByLessonNameEqualsIgnoreCase(lessonName); //türetilen method için keywordlere dikkat edilmelidir.
        //EqualsIgnoreCase case sensitivei rtadan kaldırmak için (Java , JAVA..)

        if (lessonExist){
            throw new ConflictException(String.format(ErrorMessages.LESSON_ALREADY_EXIST_WITH_LESSON_NAME,lessonName));
        } else {
            return false;
        }
    }




    public ResponseMessage deleteLesson(Long id) {

        isLessonExistById(id);//var mı yok mu kontrolü

        lessonRepository.deleteById(id);//varsa sil

       // lessonRepository.delete(isLessonExistById(id));

        return ResponseMessage.builder()
                .message(SuccessMessages.LESSON_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();


    }


    //lesson var mı yok mu kontrolü yapan yardımcı method
    public Lesson isLessonExistById(Long id){
        return lessonRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE,id)));
    }



    public ResponseMessage<LessonResponse> getLessonByLessonName(String lessonName) {

        //eğer lesson varsa, başarılı mesaj, lesson yoksa ders bulunamadı mesajı
        if(lessonRepository.getLessonByLessonName(lessonName).isPresent()){ //isPresent(), içi doluysa true döndürür
            return ResponseMessage.<LessonResponse>builder()
                    .message(SuccessMessages.LESSON_FOUND)
                    .object(lessonMapper.mapLessonToLessonResponse(lessonRepository.getLessonByLessonName(lessonName).get())) //pojodan dtoye çevirecek . if içinde repoya gitmişti burda tekrar DBye gidecek bu güzel bir kod biçimi değildir, ispresentı göstermek için böyle yazılmıştır
                    .build();
        } else {
            return ResponseMessage.<LessonResponse>builder()
                    .message(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE, lessonName))
                    //response entity kullanıyorsanız returnlerde status kodunu setlemek önemlidir ancak burda yapılmamış!!
                    .build();
        }
    }


    public Page<LessonResponse> findLessonByPage(int page, int size, String sort, String type) {
       Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);

       return lessonRepository.findAll(pageable).map(lessonMapper::mapLessonToLessonResponse);

    }

    public Set<Lesson> getLessonByLessonIdSet(Set<Long> idSet) {

        return idSet.stream() // Stream<Long>
                .map(this::isLessonExistById) //map methodunu kullandığımız zaman parantez içine geleni hemen execute olabilecek bir şey yazmanız gerekir.this; yukardan gelen idyi isLessonExistById methoduna argüman olarak gönderir.
                .collect(Collectors.toSet());
    }


    //belirli bir lessonId'ye sahip dersi güncelleyip, veritabanına kaydettikten sonra güncellenmiş dersin yanıtını döndürmek
    public LessonResponse updateLessonById(Long lessonId, LessonRequest lessonRequest) {

        Lesson lesson = isLessonExistById(lessonId);
        // !!! requeste ders ismi degisti ise unique olmasi gerekiyor kontrolu
        //isLessonExistById(lessonId) metodu, dersin veritabanında olup olmadığını kontrol eder ve eğer varsa, bu dersi döner.

        if(
                !(lesson.getLessonName().equals(lessonRequest.getLessonName())) && // requestten gelen ders ismi DB deki ders isminden farkli ise
                        (lessonRepository.existsByLessonName(lessonRequest.getLessonName())) // Derived Query
        ) {
            throw new ConflictException(
                    String.format(ErrorMessages.ALREADY_REGISTER_LESSON_MESSAGE,lessonRequest.getLessonName()));
        }
        // !!! DTO --> POJO
        Lesson updatedLesson = lessonMapper.mapLessonRequestToUpdatedLesson(lessonId, lessonRequest);

        updatedLesson.setLessonPrograms(lesson.getLessonPrograms()); //Güncelleme sırasında dersin program bilgilerinin kaybolmasını önlemek.Eğer bu adım yapılmazsa, dersin programları (lessonPrograms) güncellenen ders üzerinde null olabilir. Bu nedenle, önceki dersi (lesson) programları, yeni güncellenen ders nesnesine aktarılır.

        Lesson savedLesson = lessonRepository.save(updatedLesson); //Güncellenmiş dersi veritabanına kaydetmek

        return lessonMapper.mapLessonToLessonResponse(savedLesson);// kaydedilen Lesson nesnesini bir LessonResponse nesnesine dönüştürür ve yanıt olarak döner.
    }
}
