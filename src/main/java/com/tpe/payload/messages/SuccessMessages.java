package com.tpe.payload.messages;

public class SuccessMessages {

    private SuccessMessages() {
    } //parametresiz cons. private yaparsanız, projede bu classı newleyerek obje oluşturamazsınız. gereksiz yere obje oluşturmamak için yapılır.

    public static final String PASSWORD_CHANGED_RESPONSE_MESSAGE = "Password Successfully Changed";
}
