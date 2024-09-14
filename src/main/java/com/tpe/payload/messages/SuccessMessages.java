package com.tpe.payload.messages;

public class SuccessMessages {




    private SuccessMessages() {
    } //parametresiz cons. private yaparsanız, projede bu classı newleyerek obje oluşturamazsınız. gereksiz yere obje oluşturmamak için yapılır.

    public static final String PASSWORD_CHANGED_RESPONSE_MESSAGE = "Password Successfully Changed";

    public static final String USER_CREATED ="User is Saved";

    public static final String USER_FOUND = "User is found succesfully";

    public static final String USER_DELETE = "User is deleted successfully";
    public static final String USER_UPDATE_MESSAGE = "User is Updated Successfully";
    public static final String USER_UPDATE = "your information has been updated successfully";

    public static final String TEACHER_SAVE = "Teacher is Saved";
    public static final String TEACHER_UPDATE = "Teacher is Updated Successfully";
}
