package com.tpe.payload.messages;

public class ErrorMessages {


    public static final String PASSWORD_NOT_MATCHED = "Your passwords are not matched";
    public static final String NOT_PERMITTED_METHOD_MESSAGE = "You do not have permission to do this operation";

    public static final String ALREADY_REGISTER_MESSAGE_USERNAME ="Error: User with username: %s is already registered";
    public static final String ALREADY_REGISTER_MESSAGE_SSN ="Error: User with ssn: %s is already registered";
    public static final String ALREADY_REGISTER_MESSAGE_PHONE = "Error: User with phone number : %s is already registered";
    public static final String ALREADY_REGISTER_MESSAGE_EMAIL = "Error: User with email : %s is already registered";

    public static final String ROLE_NOT_FOUND = "There is no role  like that, check the database";
    public static final String NOT_FOUND_USER_ROLE_MESSAGE = "Error: User not found with user-role : %s";

    public static final String NOT_FOUND_USER_MESSAGE = "Error: user not found with id: %s";

    public static final String NOT_FOUND_ADVISOR_MESSAGE = "Error: Advisor Teacher with id : %s not found";

    public static final String NOT_FOUND_USER_WITH_ROLE_MESSAGE = "Error: The role information of the user with id %s is not role: %s" ;

    public static final String ALREADY_EXIST_ADVISOR_MESSAGE = "Error: Advisor Teacher with id %s is already exist";

    public static final String EDUCATION_TERM_NOT_FOUND_MESSAGE = "Error: Education Term with id : %s no found";

}
