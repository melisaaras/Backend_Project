package com.tpe.service.business;

import com.tpe.entity.concretes.business.EducationTerm;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.payload.mappers.EducationTermMapper;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.payload.messages.SuccessMessages;
import com.tpe.payload.response.ResponseMessage;
import com.tpe.payload.response.business.EducationTermResponse;
import com.tpe.repository.business.EducationTermRepository;
import com.tpe.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationTermService {

    private final EducationTermRepository educationTermRepository;
    private final EducationTermMapper educationTermMapper;
    private final PageableHelper pageableHelper;

    public EducationTermResponse getEducationTermResponseById(Long id) {
        EducationTerm term = isEducationTermExist(id);
        return educationTermMapper.mapEducationTermToEducationTermResponse(term);
    }


    private EducationTerm isEducationTermExist(Long id){
        return educationTermRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.EDUCATION_TERM_NOT_FOUND_MESSAGE, id)));
    }

    public List<EducationTermResponse> getAllEducationTerms() {
        return educationTermRepository.findAll()
                .stream()
                .map(educationTermMapper::mapEducationTermToEducationTermResponse)
                .collect(Collectors.toList());
    }

    public Page<EducationTermResponse> getAllEducationTermsByPage(int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
        return educationTermRepository.findAll(pageable)
                .map(educationTermMapper::mapEducationTermToEducationTermResponse); //direkt responsea çeviridği için collecte gerek yok
    }

    public ResponseMessage deleteEducationTermById(Long id) {
        isEducationTermExist(id);
        educationTermRepository.deleteById(id);

        return ResponseMessage.builder()
                .message(SuccessMessages.EDUCATION_TERM_DELETE) //education term silinirse, lesson programda silinecek çünkü cascadetype:all yapmıştık
                .httpStatus(HttpStatus.OK)
                .build();
    }
}