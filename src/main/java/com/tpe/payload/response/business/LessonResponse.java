package com.tpe.payload.response.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LessonResponse {

    //response olduğu için validasyonlara gerek yok.


    private Long lessonId;


    private String lessonName;


    private int creditScore;


    private boolean isCompulsory;
}
