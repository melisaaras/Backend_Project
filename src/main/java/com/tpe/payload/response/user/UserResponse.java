package com.tpe.payload.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpe.payload.response.abstracts.BaseUserResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

//kullanıcı bilgilerini içeren bir yanıt (response) nesnesidir. BaseUserResponse sınıfından türetilmiştir ve bu nedenle onun özelliklerini ve metodlarını miras alır.

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) //JSON serileştirme işlemi sırasında, değerleri null olan alanların dahil edilmemesini sağlar. Bu, JSON çıktısında sadece dolu alanların görünmesini sağlar.
@SuperBuilder
public class UserResponse extends BaseUserResponse {

    //BaseUserResponse, temel kullanıcı bilgilerini içerir ve UserResponse sınıfı bu temel bilgileri miras alarak özelleştirilmiş yanıt nesneleri oluşturur.

}
