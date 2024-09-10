package com.tpe.entity.concretes.business;

//6.ADIM: ÖĞRENCİLERİN ALDIĞI DERSLERİN BİLGİLERİNİ TUTACAĞIMIZ ENTİTY CLASSI

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tpe.entity.concretes.user.User;
import com.tpe.entity.enums.Note;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class StudentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer absentee; //yoklama bilgisi

    private Double midtermExam;

    private Double finalExam;

    private Double examAverage;
    //application.propertiesde midterm.exam.impact.percentage=0.40
    //final.exam.impact.percentage=0.60

    private String infoNote;//kanaat bilgisi

    private Note letterGrade;//7.ADIM: transcripte notların alfabetik değeri olacağı için bunları bir enum içine alacağız. bunun için bir note enum oluşturmamız gerekir.


    //8.ADIM: USER İLE STUDENTINFOYU İLİŞKİLENDİRDİK.BU CLASSI TEACHER SETLEYECEK STUDENT DA GET OLARAK GÖREBİLECEK. USERLARI ATARKEN TEACHER VE STUDENT OLARAK AYRI AYRI ATAYABİLMEK İÇİN AYRI DEĞİŞKENLER OLUŞTURDUK
    @ManyToOne
    @JsonIgnore //userdan infoyu, infodan da infoyu çağırırsanız, tostringdeki stackoverflow olmaması için bağlantıyı kopardık
    private User teacher;

    @ManyToOne//Bir öğrencinin birden fazla studentinfosu vardır.
    @JsonIgnore
    private User student;


    //11.ADIM: STUDENTINFO VE LESSON ARASINDAKİ İLİŞKİ KURDUK
    @ManyToOne
    private Lesson lesson; //studentinfo tarafından lessona baktığımız zaman one olarak görürüz. çünkü sınıftaki 20 tane öğrencinin tek lessonı vardır. çünkü lesson içindeki değerleri aynı olmaz.
    //her dersin birden fazla studentinfosu vardır.


    //14.ADIM: STUDENTINFO VE EducationTerm  ARASINDAKİ İLİŞKİ KURDUK
    @OneToOne
    private EducationTerm educationTerm;

}
