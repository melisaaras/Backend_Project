package com.tpe.entity.concretes.business;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpe.entity.concretes.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

//22.ADIM: MEET ENTİTY CLASS
@Entity

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Meet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description; //toplantı ne ile alakalı

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "US")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "US")
    private LocalTime stopTime;

    @ManyToMany //user tarafından baktığımızda birden fazla meet olabilir. meet tarafından baktığımızda ise yine birden fazla öğrenci katılabilir.
    @JoinTable( //meet hangi öğretmene ait
            name = "meet_student_table",
            joinColumns = @JoinColumn(name = "meet_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<User> studentList; //öğrenciler



    @ManyToOne(cascade = CascadeType.PERSIST) //bir rehber öğretmen birden fazla meet düzenleyebilir. bir meeti ise bir öğretmen yapabilir.
    private User advisoryTeacher; //advisory teacher. service katında rol kontrolü yapmamak için

}
