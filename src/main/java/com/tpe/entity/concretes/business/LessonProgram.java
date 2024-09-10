package com.tpe.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tpe.entity.concretes.user.User;
import com.tpe.entity.enums.Day;
import lombok.*;
import javax.persistence.*;
import java.time.LocalTime;
import java.util.Set;

//15.ADIM: DERSLERİN SAATLERİNİ TUTAN ENTİTY CLASS

@Entity

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LessonProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //16.ADIM: DAY ENUM CLASS
    @Enumerated(EnumType.STRING)
    private Day day;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "US")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "US")
    private LocalTime stopTime;


    @ManyToMany //Lesson tarafından baktığımız zaman lessonprogram manydir. aynı ders programı matematiğe de fiziğe de atayabiliriz.iki taraftan da birbirine baktığımız zaman birden fazla veri görebiliyoruz.
    @JoinTable(
            name = "lesson_program_lesson",
            joinColumns = @JoinColumn(name = "lessonprogram_id"), //lessonprogramdaki idyi tutacak olan sütun
            inverseJoinColumns = @JoinColumn(name = "lesson_id") //lesson tarafından gelecek olan lessonid bilgisini tutacak olan sütun
    )
    private Set<Lesson> lessons;
    //17.ADIM:İLİŞKİNİN TARAFINI LESSON CLASSTA MAPPEDBYLA BELİRLEMEK



    //18.ADIM:İLİŞKİNİN TARAFINI EDUCATİONTERM CLASSTA MAPPEDBYLA BELİRLEMEK
    @ManyToOne(cascade = CascadeType.PERSIST) //persist, yeni bir lessonprogram kaydedersem, educationtermi repositoryde save yapmama gerek kalmaz. hibernate otomatik olarak yapar.
    private EducationTerm educationTerm;


    //19.ADIM:
    @ManyToMany(mappedBy = "lessonProgramList", fetch = FetchType.EAGER) //userdan lessonprograma baktığımız zaman birden fazla lessonprogram olabileceği için manydir. lessonprogram tarafından baktığımız zaman lessonprogramı birden fazla user kullanıyor.
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<User> users; // fetch = FetchType.EAGER, lessonprogramı döndürürken, o lessonprogramı kullanan userlarda döndürülsün diye


    //20.ADIM: BU LESSONPROGRAMI KULLANAN KAÇ KİŞİ VARSA GETİR, BU USERLAR İÇİNDEKİ LESSONPROGRAMLARDAN SİLİNEN LESSONPROGRAMI KALDIR. BURDA BU KONTROLÜ YAPMAZSAK BÜTÜN SERVİCE KATMANLARINDA HER SEFERİNDE BU KONTROLÜ SAĞLAMAMIZ GEREKECEK

    //21.ADIM: USER CLASSA LESSONPROGRAM EKLENECEK
    @PreRemove //içinde bulunduğum entity classtan bir nesne, uygulamanın herhangi bir yerinde delete yapılacaksa bu methodu çalıştır
    private void removeLessonProgramFromUser(){
        users.forEach(user -> user.getLessonProgramList().remove(this));
    }


    //22.ADIM: MEET ENTİTY CLASS OLUŞTURACAĞIZ
}
