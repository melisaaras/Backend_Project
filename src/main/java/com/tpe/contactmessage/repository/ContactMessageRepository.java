package com.tpe.contactmessage.repository;

import com.tpe.contactmessage.dto.ContactMessageResponse;
import com.tpe.contactmessage.entity.ContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

//2.ADIM: NORMALDE SIRASIYLA CONTROLLER, SERVİCE, REPOSİTORY KATMANLARI SIRASIYLA OLUŞTURULUR. ANCAK İNJECTİONLARI YAPACAĞIMIZ İÇİN REPOSİTORYE GEÇTİK.

public interface ContactMessageRepository extends JpaRepository<ContactMessage,Long> { //JpaRepository,hazır sorgular (findById.. gibi) içerir, sorgu yazarak geliştirebiliriz, sql query yazmadan keywordlerle sorgu yazabiliriz.
    //<> içine ise, hangi entity üzerinde işlem yapacaksak o classın ismi (ContactMessage), id data type (Long)

    //19.ADIM:findByEmailEquals METHODU
    Page<ContactMessage> findByEmailEquals(String email, Pageable pageable); //Page<ContactMessage>: This indicates(göstermek) that the method returns a Page of ContactMessage entities. The Page object helps manage pagination and sorting.findByEmailEquals: This is a query method defined by Spring Data JPA. It generates a query based on the method name to find ContactMessage entities where the email field matches the specified value.String email: This parameter is the email address to search for.Pageable pageable: This parameter is used to specify pagination and sorting information. It tells the repository how to paginate and sort the results.


  // Page<ContactMessage> findBySubjectEquals(String subject, Pageable pageable);


  // Page<ContactMessage> findByDateTimeBetween( LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<ContactMessage>findBySubjectEquals(String subject, Pageable pageable);




    @Query("select c from ContactMessage c where FUNCTION('DATE', c.dateTime) between ?1 and ?2") //FUNCTION('DATE', c.dateTime) ifadesi, c.dateTime alanının tarih kısmını alır, yani zaman bilgisini atar ve sadece tarihi kullanır.
    List<ContactMessage> findMessagesBetweenDates(LocalDate beginDate, LocalDate endDate);
    //DBde localdate türü yok, localdatetime var. çünkü değişkenimizi öyle kaydetmiştik.
}
