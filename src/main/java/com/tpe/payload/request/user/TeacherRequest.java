package com.tpe.payload.request.user;

import com.tpe.payload.request.abstracts.BaseUserRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class TeacherRequest extends BaseUserRequest {

    @NotNull(message = "Please select lesson")
    private Set<Long> lessonsIdList;

    @NotNull(message = "Please select isAdvisor Teacher")
    private Boolean isAdvisorTeacher;
}