package com.sca.smartcampusbackend.repository;

import com.sca.smartcampusbackend.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByQuiz_IdOrderByQuestionNumber(Long quizId);

    @Query("SELECT COUNT(q) FROM QuizQuestion q WHERE q.quiz.id = :quizId")
    int countByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT COALESCE(SUM(q.maxMarks), 0) FROM QuizQuestion q WHERE q.quiz.id = :quizId")
    int sumMaxMarksByQuizId(@Param("quizId") Long quizId);
}
