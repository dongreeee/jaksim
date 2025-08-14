package com.hazel.jaksim.calendar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarUpdateDto {
    private Long id;
    private String title;
    private String title_color;
    private String content;
    private String sdate;
    private String edate;
}
