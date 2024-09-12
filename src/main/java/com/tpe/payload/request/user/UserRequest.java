package com.tpe.payload.request.user;

import com.tpe.payload.request.abstracts.BaseUserRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

//BaseUserRequest classından türetilmiştir. BaseUserRequest classında tanımlanan özelliklere sahip bir request objesi sağlar.BaseUserRequest'in tüm işlevlerini miras alır.
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder //parent classtan chil class ürettiyseniz, ve builder methodlarını da kullanacaksanız, parent ve child classlara @SuperBuilder anno. kullanmalısınız
public class UserRequest extends BaseUserRequest {

}