package com.tpe.repository.business;

import com.tpe.entity.concretes.business.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {


     boolean existsLessonByLessonNameEqualsIgnoreCase(String lessonName);


//optional; sistemde java ve matematik varsa ve ben fizik var mı diye soduysam nullpointerexception gelmez. içi boş bir optional lesson gelir.hem null almaktan kurtadığı için hem de obje olduğundan içindeki hazır methodları kullanmak için kullanılır.
     Optional<Lesson> getLessonByLessonName(String lessonName);


     boolean existsByLessonName(String lessonName);
}
