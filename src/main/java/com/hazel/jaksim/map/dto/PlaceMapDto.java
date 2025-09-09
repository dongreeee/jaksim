package com.hazel.jaksim.map.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PlaceMapDto {
    private Long no;
    private Long mapId;
    private String name;
    private String address;
    private Double lat;
    private Double lng;
    private String url;

    public PlaceMapDto(Long no, Long mapId, String name, String address, Double lat, Double lng, String url) {
        this.no = no;
        this.mapId = mapId;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.url = url;
    }
}
