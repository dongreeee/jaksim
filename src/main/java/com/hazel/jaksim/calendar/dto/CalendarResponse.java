package com.hazel.jaksim.calendar.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CalendarResponse {
    private Long CalendarId;
    private String titleColor;
    private String title;
    private String content;
    private String sdate;
    private String edate;
    private Boolean mapChk;
    private String fileName;

    public CalendarResponse(Long calendarId, String titleColor, String title, String content, String sdate, String edate,Boolean mapChk, String fileName) {
        CalendarId = calendarId;
        this.titleColor = titleColor;
        this.title = title;
        this.content = content;
        this.sdate = sdate;
        this.edate = edate;
        this.mapChk = mapChk;
        this.fileName = fileName;
    }


}
