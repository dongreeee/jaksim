package com.hazel.jaksim.calendar.dto;

import com.hazel.jaksim.map.dto.PlaceDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

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
    private String filename;

//    기본형(boolean, double 등)은 null을 가질 수 없고, 빈 문자열이 들어오면 변환 오류 발생
//    래퍼클래스 (Boolean, Double 등)은 null을 허용하므로 값이 없으면 null로 받음 -> 유연하게 처리 가능

    public CalendarAddDto(String titleColor, String title, String content, String sdate, String edate, String mapKeyword, String filename, List<PlaceDto>places) {
        this.titleColor = titleColor;
        this.title = title;
        this.content = content;
        this.sdate = sdate;
        this.edate = edate;
        this.mapKeyword = mapKeyword;
        this.filename = filename;
    }


}
