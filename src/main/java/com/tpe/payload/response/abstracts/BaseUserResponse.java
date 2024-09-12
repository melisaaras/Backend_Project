package com.tpe.payload.response.abstracts;

import com.tpe.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

//kullanıcıyla ilgili temel yanıt verilerini tanımlar. Bu sınıf, yanıt olarak döndürülen kullanıcı bilgilerini içerir ve UserResponse gibi sınıflar tarafından genişletilir.

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder // Builder pattern'ini kullanarak, bu sınıf ve türevlerinin nesnelerini oluşturmayı sağlar.
public abstract class BaseUserResponse {


    private Long userId;
    private String username;
    private String name;
    private String surname;
    private LocalDate birthDay;
    private String ssn;
    private String birthPlace;
    private String phoneNumber;
    private Gender gender;
    private String email;
    private String userRole;
}