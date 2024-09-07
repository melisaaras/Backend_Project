package com.tpe.entity.enums;

//3.ADIM: BİR DEFA SETLENİP SONRA DEĞİŞTİRİLEMEYECEK DEĞERLER
public enum RoleType {
    ADMIN("Admin"),
    TEACHER("Teacher"),
    STUDENT("Student"),
    MANAGER("Dean"),
    ASSISTANT_MANAGER("ViceDean");

    public final String name;

    RoleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
