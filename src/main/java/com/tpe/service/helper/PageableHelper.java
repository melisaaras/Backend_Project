package com.tpe.service.helper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PageableHelper {

    public Pageable getPageableWithProperties(int page, int size, String sort, String type){
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        if(Objects.equals(type, "desc")){
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        }
        return pageable;
    }

    //yukardaki methodla ismi aynı bu yüzden method signatureı değiştirmemiz gerekir.
    public Pageable getPageableWithProperties(int page, int size){
        return PageRequest.of(page, size, Sort.by("id").descending());
    }
}
