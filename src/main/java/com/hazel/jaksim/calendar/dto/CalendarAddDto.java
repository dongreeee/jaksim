package com.hazel.jaksim.calendar.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CalendarAddDto {

    private String titleColor;
    private String title;
    private String content;
    private String sdate;
    private String edate;
    private String mapKeyword;
    private boolean mapChk;
    private String selectedPlaceName;
    private String selectedPlaceAddress;
    private double selectedPlaceLat;

    public CalendarAddDto(String titleColor, String title, String content, String sdate, String edate, String mapKeyword, boolean mapChk, String selectedPlaceName, String selectedPlaceAddress, double selectedPlaceLat, double selectedPlaceLng, String selectedPlaceUrl) {
        this.titleColor = titleColor;
        this.title = title;
        this.content = content;
        this.sdate = sdate;
        this.edate = edate;
        this.mapKeyword = mapKeyword;
        this.mapChk = mapChk;
        this.selectedPlaceName = selectedPlaceName;
        this.selectedPlaceAddress = selectedPlaceAddress;
        this.selectedPlaceLat = selectedPlaceLat;
        this.selectedPlaceLng = selectedPlaceLng;
        this.selectedPlaceUrl = selectedPlaceUrl;
    }

    private double selectedPlaceLng;
    private String selectedPlaceUrl;

}
