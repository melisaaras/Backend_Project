package com.tpe.contactmessage.mapper;

import com.tpe.contactmessage.dto.ContactMessageRequest;
import com.tpe.contactmessage.dto.ContactMessageResponse;
import com.tpe.contactmessage.entity.ContactMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//12.ADIM: DTOLARI POJOYA, POJOLARI DTOYA DÖNÜŞTÜRECEĞİMİZ CLASSTIR.


@Component //uygulama çalıştığında componentscan anno. projede component olarak işaretlemiş olduk. classı nerde kullanmak istersek, spring frameworka contactmessage mapper nesnesi gönder demiş oluyoruz.
// Marks the class as a Spring component so that Spring can manage it and inject it where needed.
public class ContactMessageMapper {



    //dtoyu pojoya çevirme
    public ContactMessage requestToContactMessage(ContactMessageRequest contactMessageRequest){
        return ContactMessage.builder() //builder ile setter methodlarına gerek kalmadan ara bir nesne oluştururuz. değişkenlerimizi setlemiş oluruz.
                .name(contactMessageRequest.getName())
                .subject(contactMessageRequest.getSubject())
                .message(contactMessageRequest.getMessage())
                .email(contactMessageRequest.getEmail())
                .dateTime(LocalDateTime.now())
                .build();
    }



    //pojoyu dtoya çevirme
    public ContactMessageResponse contactMessageToResponse(ContactMessage contactMessage){

        return ContactMessageResponse.builder()
                .name(contactMessage.getName())
                .subject(contactMessage.getSubject())
                .message(contactMessage.getMessage())
                .email(contactMessage.getEmail())
                .dateTime(LocalDateTime.now())
                .build();
    }


}
