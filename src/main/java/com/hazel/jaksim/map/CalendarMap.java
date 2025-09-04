package com.hazel.jaksim.map;

import com.hazel.jaksim.calendar.Calendar;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "calendar_map")
public class CalendarMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "map_no")
    private Long mapNo;

    @ManyToOne
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    @ManyToOne
    @JoinColumn(name = "map_id")
    private MapInfo mapInfo;
}
