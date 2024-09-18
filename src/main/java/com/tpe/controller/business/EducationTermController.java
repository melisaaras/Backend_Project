package com.tpe.controller.business;

import com.tpe.payload.request.business.EducationTermRequest;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.business.EducationTermResponse;
import com.tpe.service.business.EducationTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/educationTerm")
@RequiredArgsConstructor
public class EducationTermController {


    private final EducationTermService educationTermService;


    //education term bilgilerini kaydetmek için
    // Not: ODEVV save() *********************************************************

    @PostMapping("/save") // /save endpoint'ine gelen POST istekleri bu metoda yönlendirilecek.
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<EducationTermResponse> saveEducationTerm (@RequestBody @Valid EducationTermRequest educationTermRequest){

        // Dönüş tipi olarak ResponseMessage kullanılmasının amacı, yanıtın standartlaştırılması ve belki de yanıtla birlikte ek bilgiler (başarı durumu, mesajlar) döndürülebilmesidir.genellikle API'ler tarafından kullanıcıya bilgi vermek, işlem sonucunu belirtmek ve bir obje döndürmek için kullanılan bir yapıdır.

        //EducationTermResponse, education terme ait bilgileri içeren bir DTO’dur

        // HTTP isteğiyle gelen JSON verisinin, belirtilen sınıfın (EducationTermRequest) bir örneğine çevrilmesini sağlar.

        //@Valid: Gelen istekteki verilerin belirtilen model (EducationTermRequest) üzerinde tanımlanmış doğrulama kurallarına uygun olup olmadığını kontrol eder

        return educationTermService.saveEducationTerm(educationTermRequest);

    }


    //education term bilgilerini IDsine göre almak için
    @GetMapping("/{id}") // http://localhost:8080/educationTerm/1 + GET
    // @GetMapping("/{id}"), URL’nin {id} kısmındaki değeri dinamik olarak yakalar ve bu değeri metodun id parametresine aktarır.
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public EducationTermResponse getEducationTermById(@PathVariable Long id){
        return educationTermService.getEducationTermResponseById(id);
        //@PathVariable, URL'deki belirli bir kısmı yakalayarak, bu değeri doğrudan metot argümanı olarak almayı sağlar.
        //Verilen id ile eğitim dönemini veritabanında bulmak ve bu bilgiyi istemciye uygun formatta (DTO) geri döndürmek.
    }

    // Not: ODEVVV updateById() ***************************************************

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PutMapping("/update/{id}")// http://localhost:8080/educationTerms/update/1 + JSON
    public ResponseMessage<EducationTermResponse>updateEducationTerm(@PathVariable Long id,
                                                                     @RequestBody @Valid EducationTermRequest educationTermRequest ){
        return educationTermService.updateEducationTerm(id,educationTermRequest);
    }


    //tüm education termleri listelemek için
    @GetMapping("/getAll") ///getAll ile sonlanan URL'lere cevap verir.
    // http://localhost:8080/educationTerm/getAll
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public List<EducationTermResponse> getAllEducationTerms(){ //birden fazla eğitim dönemi bilgisi içeren listeyi istemciye gönderir. Her bir öğe, eğitim dönemini temsil eden bir EducationTermResponse nesnesidir.
        return educationTermService.getAllEducationTerms();
    }



    @GetMapping("/getAllEducationTermsByPage")  // http://localhost:8080/educationTerm/getAllEducationTermsByPage
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public Page<EducationTermResponse> getAllEducationTermsByPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "startDate") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ){
        return educationTermService.getAllEducationTermsByPage(page, size, sort, type);
    }


    @DeleteMapping("/delete/{id}") // http://localhost:8080/educationTerm/1  + DELETE
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<?> deleteEducationTermById(@PathVariable Long id){
        return educationTermService.deleteEducationTermById(id);
    }







}
