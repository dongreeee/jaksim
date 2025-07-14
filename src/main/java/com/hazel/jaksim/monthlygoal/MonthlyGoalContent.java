package com.hazel.jaksim.monthlygoal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class MonthlyGoalContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @ManyToOne
    @JoinColumn(name = "monthlyGoal_id", foreignKey = @ForeignKey(name = "fk_monthlyGoal"))
    private MonthlyGoal monthlyGoal;
    private String content;

    @Column(name = "dateymd")
    private String dateYmd;

    @Column(name = "regdate", insertable = false, updatable = false)
    private LocalDateTime regdate;
}
