package com.hazel.jaksim.calendar;

import com.hazel.jaksim.map.MapInfo;
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

    @ManyToOne
    @JoinColumn(name = "map_id", nullable = true)
    private MapInfo mapInfo;

    @Column(name = "regdate", insertable = false, updatable = false)
    private LocalDateTime regdate;

    @Column(name="img_url")
    private String imgUrl;
}
