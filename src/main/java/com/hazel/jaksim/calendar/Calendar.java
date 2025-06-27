package com.hazel.jaksim.calendar;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String title;
    private String title_color;
    private String content;
    private String sdate;
    private String edate;

    @Column(name = "regdate", insertable = false, updatable = false)
    private LocalDateTime regdate;
}
