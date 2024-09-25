
package com.tpe.payload.response.business;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpe.entity.concretes.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeetResponse {

    private Long id;
    private String description;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime stopTime;
    private List<User> students; // List<Long> studentIds ;
    private String teacherName;
    private String teacherSsn;
    private String username;
    private Long advisorTeacherId;
}

