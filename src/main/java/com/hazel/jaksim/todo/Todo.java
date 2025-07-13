package com.hazel.jaksim.todo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String dateymd;

    private String content;

    @Column(name = "is_completed", nullable = false)
    private String isCompleted;
    private String category;

    @Column(name = "regdate", insertable = false, updatable = false)
    private LocalDateTime regdate;
    private LocalDateTime regdateUpdate;

    private String vc;
}
