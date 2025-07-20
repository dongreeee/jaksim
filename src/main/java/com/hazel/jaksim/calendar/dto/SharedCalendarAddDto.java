package com.hazel.jaksim.calendar.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SharedCalendarAddDto {

    public SharedCalendarAddDto(String titleColor, String title, String content, String sdate, String edate, String mapKeyword, boolean mapChk, Long mapId, Long messageId) {
        this.titleColor = titleColor;
        this.title = title;
        this.content = content;
        this.sdate = sdate;
        this.edate = edate;
        this.mapKeyword = mapKeyword;
        this.mapChk = mapChk;
        this.mapId = mapId;
        this.messageId = messageId;
    }

    private String titleColor;
    private String title;
    private String content;
    private String sdate;
    private String edate;
    private String mapKeyword;
    private boolean mapChk;
    private Long mapId;
    private Long messageId;


}
