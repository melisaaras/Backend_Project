package com.tpe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//projenin genelinde kullanacağımız için contactmessage package dışında yazılmıştır.
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException{

    public ConflictException(String message){
        super(message);
    }
}