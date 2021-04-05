package com.codessquad.qna.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface QuestionRepository extends PagingAndSortingRepository<Question, Long> {
    Page<Question> findAllByDeletedFalse(Pageable pageable);
}
