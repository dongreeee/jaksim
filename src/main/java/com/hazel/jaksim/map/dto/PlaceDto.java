package com.hazel.jaksim.map.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PlaceDto {
    private Long no;
    private String name;
    private String address;
    private Double lat;
    private Double lng;
    private String url;

    public PlaceDto(Long no, String name, String address, Double lat, Double lng, String url) {
        this.no = no;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.url = url;
    }

}
