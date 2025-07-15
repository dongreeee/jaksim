package com.hazel.jaksim.monthlygoal.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MonthlyGoalResponse {

    private List<String> allowedMonths;
    private List<String> goalDates;

    public MonthlyGoalResponse(List<String> allowedMonths, List<String> goalDates) {
        this.allowedMonths = allowedMonths;
        this.goalDates = goalDates;
    }
}
