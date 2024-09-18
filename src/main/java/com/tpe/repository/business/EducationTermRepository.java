package com.tpe.repository.business;

import com.tpe.entity.concretes.business.EducationTerm;
import com.tpe.entity.enums.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Arrays;
import java.util.List;

public interface EducationTermRepository extends JpaRepository<EducationTerm, Long> {

    // !!! EXTRACT --> "startDate" sütunundan yıl bilgisini alıp, bu yıl bilgisinin ikinci bağlantılı parametre olan "?2" değerine eşit olup olmadığını kontrol ediyor.
    @Query("SELECT (count (e) > 0) FROM EducationTerm e WHERE e.term=?1 AND EXTRACT(YEAR FROM e.startDate) = ?2 ")
    //EXTRACT, tarihsel ifadelerde istediğiniz şeyi getirir, ay veya yıl gibi
    boolean existsByTermAndYear(Term term, int year);

    @Query("SELECT e FROM EducationTerm e WHERE EXTRACT(YEAR FROM e.startDate) = ?1 ")
    List<EducationTerm> findByYear(int year);
}
