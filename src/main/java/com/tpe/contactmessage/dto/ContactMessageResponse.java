package com.tpe.contactmessage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

//8.ADIM: NOTNULLAR İLE NULL OLMAMASI GEREKTİĞİNİ KONTROL EDEREK VERİLERİ DBE GÖNDERDİĞİMİZ İÇİN ZATEN KESİNLİKLE NULL DEĞİLLER. PERFORMANSI OLUMSUZ ETKİLECEĞİNDEN @NOTNULL ANNO. KALDIRDIK.
// NAME,EMAİL,SUBJECT,MESSAGE,DATETİME


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ContactMessageResponse {//The ContactMessageResponse class is a data transfer object (DTO) used for sending response data about contact messages. It includes fields for the contact's name, email, subject, message, and the timestamp of when the message was sent, with the date and time formatted for JSON output.
    // iletişim mesajları hakkında yanıt verilerini göndermek için kullanılan bir veri taşıma nesnesidir (DTO). Bu sınıf, iletişim kişisinin adını, e-posta adresini, konuyu, mesajı ve mesajın gönderildiği zamanın zaman damgasını içeren alanlar içerir; tarih ve saat, JSON çıktısı için belirli bir formatta düzenlenir.)


    private String name;

    private String email;

    private String subject;

    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateTime;


}
