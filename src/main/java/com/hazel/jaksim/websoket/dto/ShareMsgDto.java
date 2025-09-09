package com.hazel.jaksim.websoket.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ShareMsgDto {
    private String username;
    private Long calendarId;


    public ShareMsgDto(String username, Long calendarId) {
        this.username = username;
        this.calendarId = calendarId;
    }
}
