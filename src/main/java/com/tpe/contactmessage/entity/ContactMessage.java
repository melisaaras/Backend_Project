package com.tpe.contactmessage.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data //Generates getters for all fields, a useful toString method, and hashCode and equals implementations that check all non-transient fields. Will also generate setters for all non-final fields, as well as a constructor.Equivalent (eşdeğer) to @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode. Since it also includes different anatotypes, @Getter @Setter can be used separately instead to avoid unnecessary workload.
@AllArgsConstructor //Generates an all-args constructor. An all-args constructor requires(gerektirmek) one argument for every field in the class
@NoArgsConstructor //Generates a no-args constructor. Will generate an error message if such a constructor cannot be written due to the existence of final fields.
@Builder(toBuilder = true) //@Builder: Lombok annotation that enables(etkinleştirir) the builder pattern for this class, making it easier to create objects in a fluent way. The toBuilder = true option allows creating a new builder from an existing object.
public class ContactMessage {

    //1.ADIM:ANONİM KULLANICININ SİSTEME GİRDİĞİ ZAMAN, SİSTEM YETKİLİLERİNE MESAJ GÖNDEREBİLMESİNİ SAĞLAYAN, FRONTEND'DEKİ REQUEST'LERİ KARŞILAYACAK OLAN CLASSTIR.ID,NAME,EMAİL,SUBJECT,MESSAGE,DATETİME

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //DBe unique olarak kaydedilmesi gerektiği için
    private Long id; //contactMessageId

    @NotNull // @NotBlank, This annotation ensures that a field is not empty (null, an empty string, or a string containing only whitespace characters)
    private String name; //contactMessageName

    @NotNull
   // @Email, @ işareti ve bu işaretten sonra string değer içerisinde nokta ve noktadan başka sonra başka karakterler var mı diye kontrol eder. düzgün bir syntax ile hiç olmayan bir emaili girdiğinizde bile sistem sizi kabul eder. Bu emailin karşılığının olup olmadığını, gerçekten kullanılıp kullanılmadığını kontrol etmez.
    // Email format validation (Commented out) This annotation validates(doğrulama) that the email field contains a valid email address. It is commented out here, likely because email validation is being handled elsewhere, such as in a DTO (Data Transfer Object) class.
    // We don't handle client requests with POJO classes; instead, we use DTOs. That's why we didn't perform email annotation validation here.
    private String email; //contactMessageEmail

    @NotNull
    private String subject; //mesajın konusu

    @NotNull
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "US") //Bir ContactMessage nesnesi oluşturduğunuzda ve bu nesneyi client'a göndermek istediğinizde, Jackson library bu nesneyi JSON formatına dönüştürecektir. Clienta giden veriyi json formatına dönüştürürken belirlediğimiz shape ve patterne göre gözükmesini isteyebiliriz.(Jackson library, Java uygulamalarında JSON verilerini serileştirmek (Java nesnelerini JSON formatına dönüştürmek) ve deserileştirmek (JSON verilerini Java nesnelerine dönüştürmek) için kullanılan popüler bir kütüphanedir.) olarak gider. JSON Serileştirme: Jackson, contactMessage nesnesini JSON formatına dönüştürürken @JsonFormat anotasyonunu kullanır.  Bu, verilerin client'a gönderildiğinde anlaşılır ve tutarlı bir formatta olmasını sağlar.
    // @JsonFormat: Customizes(özelleştirmek) the JSON format for the dateTime field when the entity is serialized or deserialized. The pattern = "yyyy-MM-dd HH:mm" specifies the date and time format, and timezone = "US" sets the time zone.
    private LocalDateTime dateTime;

}
