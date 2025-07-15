package com.hazel.jaksim.monthlygoal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonthlyGoalContentRepository extends JpaRepository<MonthlyGoalContent,Long> {

    List<MonthlyGoalContent> findByUsername(String username);
}
