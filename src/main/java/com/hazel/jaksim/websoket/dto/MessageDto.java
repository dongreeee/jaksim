package com.hazel.jaksim.websoket.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class MessageDto {

    private Long id;
    private String senderDisplayName;
    private String recieveUser;
    private LocalDateTime sendReg;
    private String vc;
    private Long calendarId;

    public MessageDto(Long id, String senderDisplayName, String recieveUser, LocalDateTime sendReg, String vc, Long calendarId) {
        this.id = id;
        this.senderDisplayName = senderDisplayName;
        this.recieveUser = recieveUser;
        this.sendReg = sendReg;
        this.vc = vc;
        this.calendarId = calendarId;
    }

}
