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
    private Long mapId;
    private String selectedPlaceName;
    private String selectedPlaceAddress;
    private double selectedPlaceLat;
    private double selectedPlaceLng;
    private String selectedPlaceUrl;

    public CalendarResponse(Long calendarId, String titleColor, String title, String content, String sdate, String edate,Boolean mapChk,Long mapId, String selectedPlaceName, String selectedPlaceAddress, double selectedPlaceLat, double selectedPlaceLng, String selectedPlaceUrl) {
        CalendarId = calendarId;
        this.titleColor = titleColor;
        this.title = title;
        this.content = content;
        this.sdate = sdate;
        this.edate = edate;
        this.mapChk = mapChk;
        this.mapId = mapId;
        this.selectedPlaceName = selectedPlaceName;
        this.selectedPlaceAddress = selectedPlaceAddress;
        this.selectedPlaceLat = selectedPlaceLat;
        this.selectedPlaceLng = selectedPlaceLng;
        this.selectedPlaceUrl = selectedPlaceUrl;
    }


}
