package com.tpe.payload.mappers;

import com.tpe.entity.concretes.business.EducationTerm;
import com.tpe.payload.response.business.EducationTermResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class EducationTermMapper {

    public EducationTermResponse mapEducationTermToEducationTermResponse(EducationTerm educationTerm){
        return EducationTermResponse.builder()
                .id(educationTerm.getId())
                .term(educationTerm.getTerm())
                .startDate(educationTerm.getStartDate())
                .endDate(educationTerm.getEndDate())
                .lastRegistrationDate(educationTerm.getLastRegistrationDate())
                .build();
    }
}
