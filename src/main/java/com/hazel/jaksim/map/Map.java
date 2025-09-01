package com.hazel.jaksim.map;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class Map {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_name")
    private String placeName;

    @Column(name = "place_url")
    private String placeUrl;

    @Column(name = "place_address")
    private String placeAddress;

    @Column(name = "place_x")
    private Double placeX;

    @Column(name = "place_y")
    private Double placeY;

    @Column(name= "regdate", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP" )
    private LocalDateTime regdate;

}
