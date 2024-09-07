package com.tpe.entity.concretes.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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






}
