package com.tpe.controller.business;

import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.business.EducationTermResponse;
import com.tpe.service.business.EducationTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/educationTerm")
@RequiredArgsConstructor
public class EducationTermController {


    private final EducationTermService educationTermService;

    // Not: ODEVV save() *********************************************************


    @GetMapping("/{id}") // http://localhost:8080/educationTerm/1 + GET
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public EducationTermResponse getEducationTermById(@PathVariable Long id){
        return educationTermService.getEducationTermResponseById(id);
    }

    // Not: ODEVVV updateById() ***************************************************



    @GetMapping("/getAll") // http://localhost:8080/educationTerm/getAll
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public List<EducationTermResponse> getAllEducationTerms(){
        return educationTermService.getAllEducationTerms();
    }



    @GetMapping("/getAllEducationTermsByPage")  // http://localhost:8080/educationTerm/getAllEducationTermsByPage
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public Page<EducationTermResponse> getAllEducationTermsByPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "startDate") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ){
        return educationTermService.getAllEducationTermsByPage(page, size, sort, type);
    }


    @DeleteMapping("/delete/{id}") // http://localhost:8080/educationTerm/1  + DELETE
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<?> deleteEducationTermById(@PathVariable Long id){
        return educationTermService.deleteEducationTermById(id);
    }







}
