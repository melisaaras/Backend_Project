package com.tpe.contactmessage.messages;

//clienta mesaj verecek string ifadeleri değiştirmek gerektiğinde kodlar arasında aramamak için messages classı oluşturulur.
public class Messages {

    //contact message
    public static final String ALREADY_SEND_A_MESSAGE_TODAY = "Error: You have already send a message with this e-mail";
    public static final String WRONG_DATE_FORMAT = "Wrong Date Format";
    public static final String WRONG_TIME_FORMAT = "Wrong Time Format";
    public static final String NOT_FOUND_MESSAGE = "Message Not Found";
    public static final String CONTACT_MESSAGE_DELETED_SUCCESSFULLY = "Contact message deleted Successfully";

    //messages nesnesinden bir instance oluşturmadan değişkeni kullanmak istediğimiz için staticlerdir.
    //bir defa setlendikleri için finaldır. uygulama çalıştığı andan itibaren bunları bir daha değiştiremezsiniz.
}
