package com.tpe.repository.business;

import com.tpe.entity.concretes.business.Meet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository //jpayı extends etmiyorsak, bu anno. eklemek zorundayız. component, repo anno. kapsar ancak comp yazmamızın sbebi, exception fırlatılması gerektiğinde repo katmanı gibi davranmarak custom exception fırlatılmasını sağlamak.
public interface MeetRepository extends JpaRepository<Meet,Long> {

    List<Meet> getByAdvisoryTeacher_IdEquals(Long advisoryTeacherId);

    List<Meet> findByStudentList_IdEquals(Long studentId);

    Page<Meet> findByAdvisoryTeacher_IdEquals(Long advisoryTeacherId, Pageable pageable);}
