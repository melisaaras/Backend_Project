package com.tpe.entity.concretes.business;

//12.ADIM: FARKLI ZAMAN DİLİMLERİNİ TEMSİL EDEN EĞİTİM DÖNEMLERİ CLASSI OLUŞTURDUK

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpe.entity.enums.Term;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EducationTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //13.ADIM: TERMLER İÇİN ENUM OLUŞTURMAMIZ GEREKİR.
    @NotNull(message = "Education term must not be empty")
    @Enumerated(EnumType.STRING)
    private Term term;

    //education term hangi tarihte başlıyor
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name="start_date")
    @NotNull(message = "Start date must not be empty")
    private LocalDate startDate;

    //education term hangi tarihte bitiyor
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name="end_date")
    @NotNull(message = "End date must not be empty")
    private LocalDate endDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name="last_registration_date")
    @NotNull(message = "Last registration date must not be empty")
    private LocalDate lastRegistrationDate;//son kayıt zamanı
}
