package com.tpe.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

//6.ADIM: FARKLI DATA TYPELERİYLE ÇALIŞABİLMESİ İÇİN, <E>

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)// Bu classtan bir instance oluşturduğumuz zaman onun içerisindeki değişkenlerden null olanlar varsa, null olanları json içerisine dahil etme. neleri json içerisine dahil edeceğimiz sorusunun cevabıdır.
// This annotation ensures that only non-null fields are included in the JSON output. If a field is null, it will be excluded from the JSON representation.null olmayan dosyaları json formayı içine dahil eder.
public class ResponseMessage <E>{
    //The ResponseMessage class is a generic data transfer object (DTO) used for sending responses in a structured format. It includes a generic object for the response data, a message for additional information, and an httpStatus for the HTTP status code. Null fields are excluded from the JSON output.
    // (ResponseMessage sınıfı, yapılandırılmış bir formatta yanıt göndermek için kullanılan genel bir veri taşıma nesnesidir (DTO). Yanıt verileri için genel bir nesne, ek bilgiler için bir mesaj ve HTTP durum kodu için bir httpStatus içerir. Null olan alanlar JSON çıktısından hariç tutulur.)

    private E object; //E data typeinde, object değişken isimli değişken

    private String message;

    private HttpStatus httpStatus;
}
