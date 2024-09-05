package com.tpe.contactmessage.controller;

import com.tpe.contactmessage.dto.ContactMessageRequest;
import com.tpe.contactmessage.dto.ContactMessageResponse;
import com.tpe.contactmessage.entity.ContactMessage;
import com.tpe.contactmessage.service.ContactMessageService;
import com.tpe.payload.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

//4.ADIM: REST MİMARİSİNİN DE ÖZELLİKLERİNİ KULLANABİLECEĞİ BİR CONTROLLER CLASSI OLUŞTURMAK.
// CONTROLLER İÇERİSİNE SERVİCE İNJECTİONI YAPMAK. BİR CLASSI İNJECTİON YAPMAK İÇİN @COMPONENT KULLANMAK GEREKİR. ÇÜNKÜ UYGULAMAYI RUN ETTİĞİMİZDE @COMPONENTSCAN İLE TARAMA YAPAR. COMPONENT İLE ANNOTE ETTİĞİMİZ METHODLARIMIZIN DÖNEN DEĞERLERİNİ VEYA CLASSLARIMII BEAN OLARAK AYIRIR. BUNUN İÇİN SERVİCE CLASSINDA COMPONENT ANNO. ZORUNLU OLMASINA RAĞMEN YAPMADIK.SPRİNG BOOT FRAMEWORK, ZORUNLU OLAN ANNOTASYONLARI, KENDİSİNİN ÜRETMİŞ OLDUĞU @SERVİCE ANNO. İÇERİSİNE GÖMEREK, UYGULAMAYA BU CLASSIN BİR BUSİNESS LOGİC CLASSI OLDUĞUNU VE BUNU COMPONENT OLARAK İŞARETLEMİŞ OLDUĞUMUZU SÖYLEMİŞ OLUYORUZ. DOLAYISIYLA CONTROLLER CLASSINDA BUNU RAHATÇA KULLANABİLİRİZ.


@RestController //Marks the class as a controller that handles HTTP requests and returns JSON responses.
@RequestMapping("/contactMessages") //Gelen requestler içerisinde contactMessages varsa (get veya post ile gelmiş olması önemli değil) direkt bu classın içerisine yönlendirir
// This annotation maps HTTP requests to handler(eşleştirmek) methods of MVC and REST controllers. Here, it specifies that all endpoints in this controller will start with /contactMessages.Sets the base URL for all endpoints in this controller to /contactMessages.
@RequiredArgsConstructor // Automatically creates a constructor for the ContactMessageService field, so it can be used in the class.
public class ContactMessageController {



    private final ContactMessageService contactMessageService; //This field holds the service layer(katman) responsible for business logic related to contact messages. It's marked as final because it's injected through the constructor and should not be reassigned(atanmak).



    @PostMapping("/save") //http://localhost:8080/contactMessages/save + POST //Bu path gelirse ve path gelirken Post mapping kullanıyorsa bu methoda yönlendiriyoruz.
    // Handles POST requests to /contactMessages/save. The method takes in a contact message request, validates it, and then saves it using the service layer. It returns a response message with the saved data.
    public ResponseMessage<ContactMessageResponse> saveContact(@RequestBody @Valid ContactMessageRequest contactMessageRequest){

        //@RequestBody: This annotation binds the HTTP request body to the contactMessageRequest object.@Valid: Ensures that the contactMessageRequest object is validated before the method is executed. If validation fails, an error response is sent automatically.ResponseMessage<ContactMessageResponse>: This method returns a custom response type that includes both the message and the contact message data.
        return contactMessageService.save(contactMessageRequest);
    } //5.ADIM: RESPONSEENTITY, OBJEYİ HTTP STATUS KODU İLE CLIENT'A MESAJ GÖNDERMEYİ SAĞLAR. ANCAK KULLANICIYA HEM BİR MESAJ HEM DE STATUS KODU GÖNDERMEK İSTERSEK, RESPONSEENTITY GİBİ ÇALIŞACAK BİR GENERIC SINIFI KENDİMİZ YAZABİLİRİZ. BÖYLECE; İSTEDİĞİMİZDE STATUS KODUNU, İSTEDİĞİMİZDE MESAJI, İSTEDİĞİMİZDE SADECE OBJEYİ DÖNDÜREBİLİRİZ. ÜÇÜNÜ DE DÖNDÜRMEK ZORUNDA KALMAYIZ. BU MESAJIN PROJENİN GENELİNDE GEREKEN BİR DURUM OLDUĞU İÇİN, PAYLOAD PAKETİ İÇİNDE RESPONSEMESSAGE SINIFINDA YAZILIR. REQUEST'TEN GELEN VEYA RESPONSE GİDECEK OLAN PAYLOAD OLABİLİR. BU YÜZDEN KARIŞIKLIĞI ÖNLEMEK İÇİN, RESPONSE OLDUĞUNU BELİRTİYORUZ.

    //7.ADIM: CLİENTA DTO GEREKTİĞİ İÇİN DTO PACKAGEINDA CONTACTMESSAGERESPONSE CLASSI OLUŞTURDUK.

    //9.ADIM:DTO PACKAGEINDA CONTACTMESSAGEREQUEST CLASSI OLUŞTURDUK.

    //11.ADIM: DTO CLASSINI SERVİCE KATINDA POJOYA DÖNÜŞTÜRMEM GEREKİR. HER METHODDA AYRI AYRI POJOYU DTOYA YA DA DTOYU POJOYA DÖNÜŞTÜRME YAPMAMAK İÇİN CONTACTMESSAGE PACKAGE- MAPPER PACKAGE-CONTACTMESSAGEMAPPER CLASSI OLUŞTURURUZ.



    //15.ADIM: BÜTÜN CONTACTMESSAGELARI PAGEABLE VERSİYONLA GETALL.SERVİCE TARAFINDA BU YAPIYI PAGEABLE OLARAK DBDEN ÇEKİLİP CLİENTA GÖNDERİLMESİNİ SAĞLAMAK İÇİN.
    @GetMapping("/getAll") //http://localhost:8080/contactMessages/getAll + GET
    // This annotation maps HTTP GET requests to the getAl method. When a GET request is made to /contactMessages/getAll, this method is executed.You can control the page number, size, sorting field, and sorting direction using query parameters.
    public Page<ContactMessageResponse> getAl(
            @RequestParam(value = "page", defaultValue = "0") int page, //kaç sayfa olsun
            @RequestParam (value = "size", defaultValue = "10") int size, ///her sayfada kaç tane veri olsun
            @RequestParam (value = "sort", defaultValue = "dateTime") String sort, //sıralama
            @RequestParam (value = "type", defaultValue = "desc") String type //sıralamayı hangi parametreye göre yapacağız

    ){

        return contactMessageService.getAll(page,size,sort,type);

    }



    //17.ADIM: EMAİL ADRESİNDEN GELEN VERİLERİ GETİR
    @GetMapping("/searchByEmail") //http://localhost:8080/contactMessages/searchByEmail?email=aaa@bb.com //Handles GET requests to /contactMessages/searchByEmail. It searches for contact messages by a specific email and returns the results in a paginated(sayfalandırılmış) format. Similar to getAll, you can control the page, size, and sorting.
    public Page<ContactMessageResponse> searchByEmail(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam (value = "size", defaultValue = "10") int size,
            @RequestParam (value = "sort", defaultValue = "dateTime") String sort,
            @RequestParam (value = "type", defaultValue = "desc") String type
    ){

        return contactMessageService.searchByEmail(email,page,size,sort,type);
    }


    // Not: *************************************** searchBySubject *************************************** //SUBJECTE GÖRE CONTACT MESSAGELERİ GETİR

    // Not: ODEVVV    searchByDateBetween *************************************** //BELLİ GÜNLER ARASINDAKİ CONTACT MESSAGELERİ GETİR

    // Not: *********************************** deleteByIdParam *************************************** (PARAM)

    // Not: ***************************************** deleteById ***************************************(PATH)

    // Not: *********************************** getByIdWithParam *************************************** CONTROLLER-SERVİCE-REPO ŞWEKLİNDE UYGULAMA UÇTAN UCA ÇALIŞIYOR OLMASI LAZIM

    // Not: ************************************ getByIdWithPath ***************************************


}
