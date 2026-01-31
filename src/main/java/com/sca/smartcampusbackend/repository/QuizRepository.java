package com.sca.smartcampusbackend.repository;

import com.sca.smartcampusbackend.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

        List<Quiz> findByOffering_Id(Long offeringId);

        @Query("SELECT DISTINCT q FROM Quiz q " +
                        "LEFT JOIN FETCH q.offering o " +
                        "LEFT JOIN FETCH o.course " +
                        "ORDER BY q.date DESC")
        List<Quiz> findAllWithOfferingAndCourse();

        @Query("SELECT q FROM Quiz q " +
                        "LEFT JOIN FETCH q.offering o " +
                        "LEFT JOIN FETCH o.course " +
                        "WHERE q.id = :id")
        Optional<Quiz> findByIdWithOfferingAndCourse(@Param("id") Long id);

        @Query("SELECT q FROM Quiz q " +
                        "LEFT JOIN FETCH q.offering o " +
                        "LEFT JOIN FETCH o.course " +
                        "LEFT JOIN FETCH q.questions " +
                        "WHERE q.id = :id")
        Optional<Quiz> findByIdWithQuestions(@Param("id") Long id);

        @Query("SELECT q FROM Quiz q " +
                        "LEFT JOIN FETCH q.offering o " +
                        "LEFT JOIN FETCH o.course " +
                        "LEFT JOIN FETCH q.attempts a " +
                        "LEFT JOIN FETCH a.user " +
                        "WHERE q.id = :id")
        Optional<Quiz> findByIdWithAttempts(@Param("id") Long id);
}
