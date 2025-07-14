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
public class MonthlyGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String title;

    @Column(name = "dateym")
    private String dateYm;

    @Column(name = "regdate", insertable = false, updatable = false)
    private LocalDateTime regdate;

}
