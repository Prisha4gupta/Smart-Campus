package com.sca.smartcampusbackend.repository;

import com.sca.smartcampusbackend.entity.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {
    List<QuizAnswer> findByAttempt_Id(Long attemptId);

    Optional<QuizAnswer> findByAttempt_IdAndQuestion_Id(Long attemptId, Long questionId);
}
