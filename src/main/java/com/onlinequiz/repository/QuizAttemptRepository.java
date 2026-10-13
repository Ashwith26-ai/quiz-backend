package com.onlinequiz.repository;

import com.onlinequiz.entity.AttemptStatus;
import com.onlinequiz.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByStudentIdOrderByStartTimeDesc(Long studentId);
    List<QuizAttempt> findByQuizIdOrderByStartTimeDesc(Long quizId);
    List<QuizAttempt> findAllByOrderByStartTimeDesc();
    List<QuizAttempt> findTop10ByOrderByStartTimeDesc();
    Optional<QuizAttempt> findFirstByStudentIdAndQuizIdAndStatus(Long studentId, Long quizId, AttemptStatus status);
    long countByStatus(AttemptStatus status);

    @Query("SELECT AVG(qa.percentage) FROM QuizAttempt qa WHERE qa.status != 'IN_PROGRESS'")
    Double getAveragePercentage();
}
