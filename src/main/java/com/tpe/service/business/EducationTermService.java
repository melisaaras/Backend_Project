package com.tpe.service.business;

import com.tpe.entity.concretes.business.EducationTerm;
import com.tpe.exception.BadRequestException;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.payload.mappers.EducationTermMapper;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.payload.messages.SuccessMessages;
import com.tpe.payload.request.business.EducationTermRequest;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.business.EducationTermResponse;
import com.tpe.repository.business.EducationTermRepository;
import com.tpe.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationTermService {

    private final EducationTermRepository educationTermRepository;
    private final EducationTermMapper educationTermMapper;
    private final PageableHelper pageableHelper;


    public ResponseMessage<EducationTermResponse> saveEducationTerm(EducationTermRequest educationTermRequest) {

        validateEducationTermDates(educationTermRequest); // Eğitim dönemi tarihlerinin geçerli olup olmadığını kontrol eder. Eğitim döneminin başlangıç tarihi bitiş tarihinden önce olmalıdır ve tarihler mantıksal olarak tutarlı olmalıdır.

        EducationTerm savedEducationTerm = educationTermRepository.save(educationTermMapper.mapEducationTermRequestToEducationTerm(educationTermRequest));
        //mapper ile, EducationTermRequest nesnesini, DBde kullanılabilecek bir EducationTerm nesnesine dönüştürür. Mapper, iki farklı yapıdaki nesne arasında veri aktarımı sağlar.
        // Dönüşümü tamamlanan EducationTerm nesnesi, JPA repository kullanılarak veritabanına kaydedilir.

        return ResponseMessage.<EducationTermResponse>builder()
                .message(SuccessMessages.EDUCATION_TERM_SAVE) // eğitim döneminin başarıyla kaydedildiğine dair bir mesaj
                .object(educationTermMapper.mapEducationTermToEducationTermResponse(savedEducationTerm))
                .httpStatus(HttpStatus.CREATED)
                .build();
    }




    private void validateEducationTermDatesForRequest(EducationTermRequest educationTermRequest){
        // !!! bu metodda amacimiz requestten gelen registrationDate,StartDate ve endDate arasindaki
        // tarih sirasina gore dogru mu setlenmis onu kontrol etmek

        // registration > start
        if(educationTermRequest.getLastRegistrationDate().isAfter(educationTermRequest.getStartDate())){
            throw new ResourceNotFoundException(
                    ErrorMessages.EDUCATION_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE);
        }
        // end > start
        if(educationTermRequest.getEndDate().isBefore(educationTermRequest.getStartDate())){
            throw new ResourceNotFoundException(
                    ErrorMessages.EDUCATION_END_DATE_IS_EARLIER_THAN_START_DATE);
        }
    }




    // !!! yrd Metod - 3 ********************************************************************
    private void validateEducationTermDates(EducationTermRequest educationTermRequest){


        validateEducationTermDatesForRequest(educationTermRequest); // Yrd Method - 2

        // !!! Bir yil icinde bir tane Guz donemi veya Yaz Donemi olmali kontrolu
        if(educationTermRepository.existsByTermAndYear( // JPQL
                educationTermRequest.getTerm(),educationTermRequest.getStartDate().getYear())){
            throw new ResourceNotFoundException(
                    ErrorMessages.EDUCATION_TERM_IS_ALREADY_EXIST_BY_TERM_AND_YEAR_MESSAGE);
        }


        // !!! yil icine eklencek educationTerm, mevcuttakilerin tarihleri ile cakismamali ****************************
        if(educationTermRepository.findByYear(educationTermRequest.getStartDate().getYear())
                .stream()
                .anyMatch(educationTerm -> //anyMatch; aşağıdaki durumlardan biri varsa, BadRequestException fırlat
                        (			educationTerm.getStartDate().equals(educationTermRequest.getStartDate()) //!!! 1. kontrol : baslama tarihleri ayni ise --> et1(10 kasim 2023) / YeniEt(10 kasim 2023)
                                || (educationTerm.getStartDate().isBefore(educationTermRequest.getStartDate())//!!! 2. kontrol : baslama tarihi mevcuttun baslama ve bitis tarihi ortasinda ise -->
                                && educationTerm.getEndDate().isAfter(educationTermRequest.getStartDate())) // Ornek : et1 ( baslama 10 kasim 2023 - bitme 20 kasim 2023)  - YeniEt ( baslama 15 kasim 2023 bitme 25 kasim 2023)
                                || (educationTerm.getStartDate().isBefore(educationTermRequest.getEndDate()) //!!! 3. kontrol bitis tarihi mevcuttun baslama ve bitis tarihi ortasinda ise
                                && educationTerm.getEndDate().isAfter(educationTermRequest.getEndDate()))// Ornek : et1 ( baslama 10 kasim 20203 - bitme 20 kasim 20203)  - YeniEt ( baslama 09 kasim 2023 bitme 15 kasim 20203)
                                || (educationTerm.getStartDate().isAfter(educationTermRequest.getStartDate()) //!!!4.kontrol : yeni eklenecek eskiyi tamamen kapsiyorsa
                                && educationTerm.getEndDate().isBefore(educationTermRequest.getEndDate()))//et1 ( baslama 10 kasim 20203 - bitme 20 kasim 20203)  - YeniEt ( baslama 09 kasim 2023 bitme 25 kasim 20203)
                        ))
        ) {
            throw new BadRequestException(ErrorMessages.EDUCATION_TERM_CONFLICT_MESSAGE);
        }
    }


    //ID’ye göre eğitim dönemini bulur ve bunu bir EducationTermResponse nesnesine dönüştürür.
    public EducationTermResponse getEducationTermResponseById(Long id) {
        EducationTerm term = isEducationTermExist(id);
        return educationTermMapper.mapEducationTermToEducationTermResponse(term);
    }


    private EducationTerm isEducationTermExist(Long id){
        return educationTermRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.EDUCATION_TERM_NOT_FOUND_MESSAGE, id)));
    }


    //isEducationTermExist gömerek encapsulation yaparak çağırdık. başka birservicee education term döndüren bir methodu kullanım yetkisi veriyoruz, hangi değişkenlerin kullanıldığını, exception fırklatıp fırlatmadığıyla ilgili bilgi vermiyoruz. yukardaki method private olduğunu için de görünmez. private olan methodları public methodlar içinde gömerek kullanıma sunmakla encapsulation yapmış oluruz.
    public EducationTerm getEducationTermById(Long id){
        return isEducationTermExist(id);
    }

    public List<EducationTermResponse> getAllEducationTerms() {
        return educationTermRepository.findAll()//tüm eğitim dönemlerini veritabanından alır ve bunları DTO'ya (EducationTermResponse) dönüştürerek istemciye döner.
                .stream()
                .map(educationTermMapper::mapEducationTermToEducationTermResponse)
                .collect(Collectors.toList());
    }

    public Page<EducationTermResponse> getAllEducationTermsByPage(int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
        return educationTermRepository.findAll(pageable)
                .map(educationTermMapper::mapEducationTermToEducationTermResponse); //direkt responsea çeviridği için collecte gerek yok
    }

    public ResponseMessage deleteEducationTermById(Long id) {
        isEducationTermExist(id);
        educationTermRepository.deleteById(id);

        return ResponseMessage.builder()
                .message(SuccessMessages.EDUCATION_TERM_DELETE) //education term silinirse, lesson programda silinecek çünkü cascadetype:all yapmıştık
                .httpStatus(HttpStatus.OK)
                .build();
    }


    public ResponseMessage<EducationTermResponse>updateEducationTerm(Long id,EducationTermRequest educationTermRequest){
        // !!! ıd var mı ???
        isEducationTermExist(id);
        // !!! gırılen tarıhler dogru mu ???
        validateEducationTermDates(educationTermRequest);

        EducationTerm educationTermUpdated =
                educationTermRepository.save(
                        educationTermMapper.mapEducationTermRequestToUpdatedEducationTerm(id,educationTermRequest));

        return ResponseMessage.<EducationTermResponse>builder()
                .message(SuccessMessages.EDUCATION_TERM_UPDATE)
                .httpStatus(HttpStatus.OK)
                .object(educationTermMapper.mapEducationTermToEducationTermResponse(educationTermUpdated))
                .build();
    }
}