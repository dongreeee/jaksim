package com.hazel.jaksim.monthlygoal.dto;

import com.hazel.jaksim.monthlygoal.MonthlyGoalContent;
import com.hazel.jaksim.monthlygoal.MonthlyGoalContentRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MonthlyGoalViewDataDto {
    private Long id;
    private String title;
    private String dateYm;
    private List<MonthlyGoalContent> contents;


    public MonthlyGoalViewDataDto(Long id, String title, String dateYm, List<MonthlyGoalContent> contents) {
        this.id = id;
        this.title = title;
        this.dateYm = dateYm;
        this.contents = contents;
    }

}
