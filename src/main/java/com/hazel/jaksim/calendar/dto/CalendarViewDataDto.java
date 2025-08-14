package com.hazel.jaksim.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CalendarViewDataDto {
    private final CalendarResponse dto;
    private final Long msgId;
    private final boolean sharedChk;
}
