package com.tpe.contactmessage.controller;

import com.tpe.contactmessage.dto.ContactMessageRequest;
import com.tpe.contactmessage.dto.ContactMessageResponse;
import com.tpe.contactmessage.entity.ContactMessage;
import com.tpe.contactmessage.service.ContactMessageService;
import com.tpe.payload.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

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


    //21.ADIM:
    // Not: *************************************** searchBySubject *************************************** //SUBJECTE GÖRE CONTACT MESSAGELERİ GETİR
   /* @GetMapping("/searchBySubject") http://localhost:8080/contactMessages/searchBySubject + GET
    public Page<ContactMessageResponse> searchBySubject(
            @RequestParam(value = "subject") String subject,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam (value = "size", defaultValue = "10") int size,
            @RequestParam (value = "sort", defaultValue = "dateTime") String sort,
            @RequestParam (value = "type", defaultValue = "desc") String type
    ){
        return contactMessageService.searchBySubject(subject,page,size,sort,type);
    }*/

    @GetMapping("/searchBySubject")// http://localhost:8080/contactMessages/searchBySubject?subject=deneme
    public Page<ContactMessageResponse> searchBySubject(
            @RequestParam(value = "subject") String subject,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "sort",defaultValue = "dateTime") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type){
        return contactMessageService.searchBySubject(subject,page,size,sort,type);
    }


    //23.ADIM:
    // Not: ODEVVV    searchByDateBetween *************************************** //BELLİ GÜNLER ARASINDAKİ CONTACT MESSAGELERİ GETİR
  /* @GetMapping("/searchByDateBetween") http://localhost:8080/contactMessages/searchByDateBetween + GET
   public Page<ContactMessageResponse> searchByDateBetween(
       @RequestParam(value = "startDate")@DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
       @RequestParam(value = "endDate")@DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)LocalDateTime endDate,
       @RequestParam(value = "page", defaultValue = "0") int page,
       @RequestParam (value = "size", defaultValue = "10") int size,
       @RequestParam (value = "sort", defaultValue = "dateTime") String sort,
       @RequestParam (value = "type", defaultValue = "desc") String type){


        return contactMessageService.searchByDateBetween(startDate,endDate,page,size,sort,type);
    }*/

    @GetMapping("/searchBetweenDates") // http://localhost:8080/contactMessages/searchBetweenDates?beginDate=2023-09-13&endDate=2023-09-15
    public ResponseEntity<List<ContactMessage>> searchByDateBetween( //Clienta döndürdüğünüz nesnelerde, gizli tutulmasını istediğiniz bilgiler yoksa (password gibi) entity döndürebiliriz.
            @RequestParam(value = "beginDate") String beginDateString,
            @RequestParam(value = "endDate") String endDateString){
        List<ContactMessage>contactMessages = contactMessageService.searchByDateBetween(beginDateString, endDateString);
        return ResponseEntity.ok(contactMessages);
    }


    // Not: *********************************** deleteByIdParam *************************************** (PARAM)
  /* @DeleteMapping("/deleteByIdParam")//http://localhost:8080/contactMessages/deleteByIdParam?id=1 + DELETE

   public ResponseMessage<ContactMessageResponse> deleteByIdParam(@RequestParam (value = "id") Long id){
       return contactMessageService.deleteById(id);

   }*/

  @DeleteMapping("/deleteByIdParam")  //http://localhost:8080/contactMessages/deleteByIdParam?contactMessageId=1
  public ResponseEntity<String> deleteById(@RequestParam(value = "contactMessageId") Long contactMessageId){ //aslında deletelerde string ifade veya entity döndürmeyz, http ststus kod 204 döndürürüz.
      return ResponseEntity.ok(contactMessageService.deleteById(contactMessageId));
  }



    // Not: ***************************************** deleteById ***************************************(PATH)
   /* @DeleteMapping ("/deleteById/{id}")//http://localhost:8080/contactMessages/deleteById/1 + DELETE
    public ResponseMessage<ContactMessageResponse> deleteById(@PathVariable("id") Long id){

        return  contactMessageService.deleteById(id);
    }*/

    @DeleteMapping("/deleteById/{contactMessageId}")//http://localhost:8080/contactMessages/deleteById/2
    public ResponseEntity<String> deleteByIdPath(@PathVariable Long contactMessageId){
        return ResponseEntity.ok(contactMessageService.deleteById(contactMessageId));
    }



    // Not: *********************************** getByIdWithParam *************************************** CONTROLLER-SERVİCE-REPO ŞEKLİNDE UYGULAMA UÇTAN UCA ÇALIŞIYOR OLMASI LAZIM

  /* @GetMapping("/getByIdWithParam")//http://localhost:8080/contactMessages/getByIdWithParam?id=1 + GET

   public ResponseMessage<ContactMessageResponse>  getByIdWithParam(@RequestParam (value = "id" )Long id){
       return contactMessageService.getById(id);
   } */

    @GetMapping("/getByIdParam") //http://localhost:8080/contactMessages/getByIdParam?contactMessageId=1
    public ResponseEntity<ContactMessage> getById(@RequestParam(value = "contactMessageId") Long contactMessageId){
        return ResponseEntity.ok(contactMessageService.getContactMessageById(contactMessageId));
    }


    // Not: ************************************ getByIdWithPath ***************************************
  /*  @GetMapping("/getByIdWithPath/{id}")//http://localhost:8080/contactMessages/getByIdWithPath/1 + GET

  public ResponseMessage<ContactMessageResponse>  getByIdWithPath(@PathVariable("id") Long id){

        return contactMessageService.getById(id);
    }*/

    @GetMapping("/getById/{contactMessageId}")//http://localhost:8080/contactMessages/getById/1
    public ResponseEntity<ContactMessage> getByIdPath(@PathVariable Long contactMessageId){
        return ResponseEntity.ok(contactMessageService.getContactMessageById(contactMessageId));
    }

}
