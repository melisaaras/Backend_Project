package com.tpe.contactmessage.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

//10.ADIM: RESPONSE DTOLARINDA VALİDATİONA GEREK YOK ANCAK REQUEST DTOLARINDA KESİNLİKLE VALİDATİON YAPMALIYIZ. ÇÜNKÜ REQUESTLER KULLANICIDAN GELİYOR BUNLARI KONTROL ETMEMİZ GEREKİR.
// NAME,EMAİL,SUBJECT,MESSAGE (DATETİMEI SİLDİK ÇÜNKÜ KULLANICI TARİHİ YANLIŞ GİREBİLİR BU YÜZDEN BUNU KULLANICIDAN ALMAYA GEREK YOK. ZATEN SERVİCE KATMANINDA KENDİMİZ EKLİYORUZ.

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ContactMessageRequest { //The ContactMessageRequest class is a data transfer object (DTO) used for capturing(yakalamak) and validating(doğrulamak) user input for contact messages. It includes validation rules to ensure that the data is complete and correctly formatted before it is processed or saved.
    //  iletişim mesajları için kullanıcı girişlerini yakalamak ve doğrulamak amacıyla kullanılan bir veri taşıma nesnesidir (DTO). Bu sınıf, verilerin işlenmeden veya kaydedilmeden önce eksiksiz ve doğru formatta olduğundan emin olmak için doğrulama kuralları içerir.)

    @NotNull(message = "Please enter name")
    @Size(min = 3, max = 16, message = "Your name should be at least 3 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your name must consist of the character .") //alfabetik karakter içermesini sağlar.
    private String name;

    @NotNull(message = "Please enter email")
    @Size(min = 5, max = 20, message = "Your email should be at least 5 chars")
    @Email(message = "Please enter valid email") //request classı clienttan gelen requesti karşılacağı için @email anno ile kontrolü burda sağlayabiliriz.
    private String email;

    @NotNull(message = "Please enter subject")
    @Size(min = 4, max = 50, message = "Your subject should be at least 4 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your subject must consist of the character .")
    private String subject;

    @NotNull(message = "Please enter message")
    @Size(min = 4, max = 50, message = "Your message should be at least 4 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your message must consist of the character .")
    private String message;


}
