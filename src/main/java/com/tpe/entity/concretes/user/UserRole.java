package com.tpe.entity.concretes.user;

import com.tpe.entity.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

//4.ADIM:ENUM TYPRİN İÇİNDE DEĞİŞİKLİK YAPMAK İSTERSEK, USER DİREKT ETKİLENMEZ. ÇÜNKÜ USER, DİREKT OLARAK ENUMTYPE BAĞIMLI DEĞİLDİR. CLASS İÇİNDE ROLETYPE İSİMLİ BİR DEĞİŞKEN YOK. BAĞIMLILIK DURUMUNU ORTADAN KALDIRMAK İÇİN ARAYA BİR CONCRETE CLASS YAZIYORUZ.
@Entity
@Table(name = "roles")

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//roleId

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoleType roleType;

    private String roleName;

}
