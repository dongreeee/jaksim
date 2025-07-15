package com.hazel.jaksim.monthlygoal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MonthlyGoalRepository extends JpaRepository<MonthlyGoal, Long> {
    Optional<MonthlyGoal> findByUsernameAndDateYm(String username, String dateym);
}
