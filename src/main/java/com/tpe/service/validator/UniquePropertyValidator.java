package com.tpe.service.validator;

import com.tpe.exception.ConflictException;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

//unique olmasını bekledğim değerleri valide eden classtır

@Component
@RequiredArgsConstructor
public class UniquePropertyValidator {

    private final UserRepository userRepository;

    //uniquelik kontrolü
    public void checkDuplicate(String username, String ssn, String phone, String email){

        if (userRepository.existsByUsername(username)){ //parametreden gelen username repodaki recordda varsa exception fırlatılır.
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_USERNAME,username)); //ConflictException(ErrorMessages.ALREADY_REGISTER_MESSAGE_USERNAME,username)compile time error verir çünkü conflictexception iki parametre kabul etmez bu yüzden formatladık. formatlı halinde ilk parametreye genel ifade, ikinci parametreye ise bu genel ifade içerisinde kullanacağınız yer tutucu yerine geçecek olan değişkeni koyduk
        }

        if (userRepository.existsBySsn(ssn)){
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_SSN,ssn));
        }

        if (userRepository.existsByPhoneNumber(phone)){
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_PHONE,phone));
        }

        if (userRepository.existsByEmail(email)){
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_EMAIL,email));
        }



    }
}
