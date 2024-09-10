package com.tpe.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

//10.ADIM:DERSLERİ TUTACAĞIMIZ ENTİTY CLASSIDIR.
@Entity

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lessonId;

    private String lessonName;

    private Integer creditScore;

    private Boolean isCompulsory; //dersin zorunluluğu

    //17.ADIM:İLİŞKİNİN TARAFINI MAPPEDBYLA BELİRLEMEK
    @JsonIgnore
    @ManyToMany(mappedBy = "lessons", cascade = CascadeType.REMOVE) //cascade = CascadeType.REMOVE; lesson silinirse, onun lessonprogramı da silinsin.
    private Set<LessonProgram> lessonPrograms;






}
