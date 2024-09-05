package com.tpe.contactmessage.service;
import com.tpe.contactmessage.dto.ContactMessageRequest;
import com.tpe.contactmessage.dto.ContactMessageResponse;
import com.tpe.contactmessage.entity.ContactMessage;
import com.tpe.contactmessage.mapper.ContactMessageMapper;
import com.tpe.contactmessage.repository.ContactMessageRepository;
import com.tpe.payload.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

//3.ADIM: SERVİCE KATMANINDA REPOSİTORY İLE KONUŞULACAĞI İÇİN contactMessageRepository DEĞİŞKENİ OLUŞTURMA

@Service //This marks the class as a service in the Spring framework, which means it contains business logic.
@RequiredArgsConstructor //This automatically creates a constructor to inject the dependencies (contactMessageRepository and createContactMessage).
// final keywordüyle setlediğiniz değişkenlerden parametreli cons oluşturur. Dolaylı olarak cons injectionı da sağlamış olur. parametresiz olanları dahil etmez. (classtaki bütün değişkenlerden cons oluşturmasını isteseydik allargconstructor kullanabilirdik.)
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository; //repository katmanıyla konuşulacağı için
    //This field is for interacting with the database. It allows you to save, find, and retrieve contact messages.


    //13.ADIM: ContactMessageMapperIN İNJECTİON İŞLEMİ İÇİN DEĞİŞKEN OLUŞTURDUK
    private final ContactMessageMapper createContactMessage;//This field is for converting between ContactMessageRequest, ContactMessageResponse, and ContactMessage entities. It helps with mapping data between different layers.


    //14.ADIM:SAVE İŞLEMİ
    public ResponseMessage<ContactMessageResponse> save(ContactMessageRequest contactMessageRequest) { //Converts the request data into a ContactMessage entity.Saves the entity to the database.Returns a success message along with the saved data.
        ContactMessage contactMessage =  createContactMessage.requestToContactMessage(contactMessageRequest);
        ContactMessage savedData =  contactMessageRepository.save(contactMessage);

        return ResponseMessage.<ContactMessageResponse>builder() //Responsemessage ile sarmalama yapmamız lazım
                .message("Contact Message Created Successfully")
                .httpStatus(HttpStatus.CREATED) // 201
                .object(createContactMessage.contactMessageToResponse(savedData)) //pojoyu dtoya çevirme
                .build();
    }

    //15.ADIM: CONTACTMESAAGECONTROLLER CLASSINA GİT


    //16.ADIM: JPAYI KULLANARAK PAGEABLE YAPIDA VERİ ÇEKMEK İSTİYORSANIZ BU İŞLEM İÇİN PAGEABLE DATA TYPEİNDEKİ VERİYİ KULLANARAK GELİNİRSE, SAYFALAMA TEKNOLOJİİSYLE VERİLERİ SANA SUNABİLİRİM DEMEKTİR.  YANİ REPOSİTORY KATAMNINDA TALEPTE BULUNABİLMEMİZ İÇİN ELİMİZDE PAGEABLE NESNE OLMASI LAZIM.
    //PAGE YAPIDA İÇERİSİNDE CONTACTMESSAGERESPONSE OLAN DTO CLASSLARI ELDE EDİLMESİ İÇİN.
    public Page<ContactMessageResponse> getAll(int page, int size, String sort, String type) { //Creates a pageable object to manage pagination and sorting.Checks if sorting should be ascending or descending.Retrieves all contact messages from the database and converts them to ContactMessageResponse objects.Returns the paginated result.

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending()); //sayfalama teknolojisiyle yazıyorsak, data domainden import etmemiz gerekir. ascending olarak sıralanmasını istedik.

        //type değeri asc değil de desc ise, desc olarak gönder dedik.
        if (Objects.equals(type, "desc")){ //object classının equals methodu, null safetir. type null bile olsa kıyaslama yapar ve null döndürür.
            //type.equals("desc")-->nesnenin equals methodunu çağırırsanız, type null iken, null olan değeri ilşeleme soktuğumuzdan NullPointerException  alırsınız. yani null safe değildir.
            //iki veriyi kıyaslayacaksanız, null safe olanları yapmalısınız. null olma ihtimali olan bir durum varsa, equals methodunu çalıştırmayın
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());

        }

        return contactMessageRepository.findAll(pageable).map(createContactMessage::contactMessageToResponse); //bütün contactmessageleri sayafalama teknolojisiyle gönderir.
        //map, tür dönüşümünü sağlar. findalla gelen bütün pojoları createContactMessage classının contactMessageToResponse, methoduna argüman olarak döndürür ve ordan gelen DTOları returnlüyor.

    }


    //17.ADIM: CONTACTMESSAGECONTROLLER CLASSINA GİT



    //18.ADIM:SEARCHBYEMAİL METHODU
    public Page<ContactMessageResponse> searchByEmail(String email, int page, int size, String sort, String type) { //Similar to getAll, but this method filters contact messages by email.Retrieves the matching contact messages from the database, converts them to ContactMessageResponse, and returns the paginated result.

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        if (Objects.equals(type, "desc")){
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());

        }

        //19.ADIM: REPOSİTORY KATMANINA GİT

        //20.ADIM: contactMessageRepository.findByEmailEquals(email,pageable)->POJOYU
        // map(createContactMessage::contactMessageToResponse)->DTOYA ÇEVİRME
        return contactMessageRepository.findByEmailEquals(email,pageable).map(createContactMessage::contactMessageToResponse);

    }
}
