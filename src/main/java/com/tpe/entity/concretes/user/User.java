package com.tpe.entity.concretes.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tpe.entity.concretes.business.LessonProgram;
import com.tpe.entity.concretes.business.Meet;
import com.tpe.entity.concretes.business.StudentInfo;
import com.tpe.entity.enums.Gender;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


//1.ADIM: DBDEKİ TABLOMUZU DİZAYN EDİYORUZ.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true) //nesneyi klonlayıp üzerine setleme işlemi yapmayı sağlar.var olan bir nesneyi baz alarak yeni bir nesne oluşturmayı sağlar. Bu özellik, var olan nesneyi klonlayıp sadece belirli alanları değiştirerek yeni bir nesne oluşturmak istediğinizde kullanışlıdır.

@Entity
@Table(name = "t_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String ssn; //kimlik numarası, aritmetik işleme sokmayacağımız için string olarak alıyoruz.

    private String name;

    private String surname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDay;

    private String birthPlace;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) //clienttan gelen data json formatında yazılsın ancak DBden gelen data artık read olduğu için clienta gittiği zaman dahil edilmesin.
    private String password;

    @Column(unique = true)
    private String phoneNumber;

    @Column(unique = true)
    private String email;

    private Boolean built_in; //Adminler bütün kullanıcıları silme yetkisine de sahip olduğu için admin kendini de silerse, uygulama işlevsiz hale gelir ve kullanılamaz. Sistemde bir tane built_in admin olur ve bu admin silinemez.
    //Service katmanında, deleteuser ya da updateuser methodlarında silmek veya güncellemek istediğiniz adminin built_in değeri true mu false mu kontrolü yapmak zorundayız. False ise, silinebilir ya da güncellenebilir ancak; true ise dokunulamazdır.


    //rolleri entity olarak yazmadığımız için custom bir user belirlemiş olduk ve bu userdaki değişkenler bütün kullanıcı bilgilerine hitap etmelidir.
    private String motherName;

    private String fatherName;

    private int studentNumber;

    private boolean isActive; //öğrencinin kaydını dondurmuş olması durumu olabileceği için.
    //aslında non-rimitive olarak Boolean yazmak daha doğrudur ancak getter methodlarında primitivelerde sorun yaşandığını görmek için primitive olarak aldık.

    private Long advisorTeacherId; //öğrencinin rehber öğretmeninin idsi

    private Boolean isAdvisor; //öğretmenin advisor olup olmadığının kontrolünü sağlamak için

    @Enumerated(EnumType.STRING) //EnumType.STRING bunu yazmazsanız, gender bilgisi 0 (ilk değişken) ve 1(ikinci değişken) olarak kaydedilir. bunun olmaması için string yazılır ve ifade male ya da female olarak gelir.
    private Gender gender; //string olursa kullanıcı erkek,ERKEK olarak farklı farklı girilebileceği için bu tarz ifadeleri enum yapmamız daha doğru olur. böylece uygulama başında bir defa setleriz. kullanıcı o setlediğiniz seçeneklerden birini seçer ve bir daha da değiştirmez. enum tipini değiştirebilir yani kadın yapabilir ancak seçenekleri değiştiremez.


    //5.ADIM: USER CLASS İLE USERROLE CLASS ARASINDA İLİŞKİ TANIMLAMAK
    @OneToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UserRole userRole;



    //9.ADIM:
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.REMOVE) //teacher isimli değişken hangi classtaysa ilişkiyi o yönetsin. teacher veya student yazmamız fark etmez.
    private List<StudentInfo> studentInfos;



    //21.ADIM:USER CLASSA LESSONPROGRAM EKLENECEK
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "user_lessonprogram",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "lesson_program_id")
    )
    private Set<LessonProgram> lessonProgramList;

    @ManyToMany //bir meeti birden fazla usera atayabiliriz, bir userı da birden fazla meeta atayabiliriz.
    @JsonIgnore
    @JoinTable(
            name = "meet_student_table",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "meet_id")

    )

    private List<Meet> meetList;
//meet tarafındaki jointable ile aynı tabloyu oluşturur. çünkü sütun isimleri aynı
}
