package com.sca.smartcampusbackend.repository;

import com.sca.smartcampusbackend.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    Optional<QuizAttempt> findByQuiz_IdAndUser_Id(Long quizId, Long userId);

    List<QuizAttempt> findByQuiz_Id(Long quizId);

    @Query("SELECT a FROM QuizAttempt a " +
            "LEFT JOIN FETCH a.user " +
            "WHERE a.quiz.id = :quizId")
    List<QuizAttempt> findByQuizIdWithUser(@Param("quizId") Long quizId);

    @Query("SELECT a FROM QuizAttempt a " +
            "LEFT JOIN FETCH a.user " +
            "LEFT JOIN FETCH a.answers ans " +
            "LEFT JOIN FETCH ans.question " +
            "WHERE a.id = :attemptId")
    Optional<QuizAttempt> findByIdWithAnswers(@Param("attemptId") Long attemptId);
}
