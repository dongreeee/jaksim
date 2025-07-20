package com.hazel.jaksim.map;

import com.hazel.jaksim.calendar.dto.CalendarAddDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MapService {

    private final MapRepository mapRepository;


    public Optional<Map> addMap(CalendarAddDto formDto){

        Long map_id;


        Map map = new Map();
        map.setPlaceAddress(formDto.getSelectedPlaceAddress());
        map.setPlaceName(formDto.getSelectedPlaceName());
        map.setPlaceUrl(formDto.getSelectedPlaceUrl());
        map.setPlaceX(formDto.getSelectedPlaceLng());
        map.setPlaceY(formDto.getSelectedPlaceLat());
        mapRepository.save(map);
        map_id = map.getId();



        Optional<Map> newMap = mapRepository.findById(map_id);

        return newMap;

    }

}
