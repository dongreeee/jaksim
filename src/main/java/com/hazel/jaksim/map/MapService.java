package com.hazel.jaksim.map;

import com.hazel.jaksim.calendar.Calendar;
import com.hazel.jaksim.calendar.dto.CalendarAddDto;
import com.hazel.jaksim.map.dto.PlaceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MapService {

    private final MapInfoRepository mapInfoRepository;
    private final CalendarMapRepository calendarMapRepository;

    public MapInfo addMap(PlaceDto placeDto){

        MapInfo mapInfo = new MapInfo();
        mapInfo.setPlaceAddress(placeDto.getAddress());
        mapInfo.setPlaceName(placeDto.getName());
        mapInfo.setPlaceUrl(placeDto.getUrl());
        mapInfo.setPlaceX(placeDto.getLng());
        mapInfo.setPlaceY(placeDto.getLat());

        return mapInfoRepository.save(mapInfo);


    }

    public void addCalendarMap(Long no, Calendar calendar, MapInfo mapInfo){

        CalendarMap calendarMap = new CalendarMap();
        calendarMap.setMapNo(no);
        calendarMap.setCalendar(calendar);
        calendarMap.setMapInfo(mapInfo);
        // 필요하면 no를 CalendarMap에 저장 (선택)
        calendarMapRepository.save(calendarMap);

    }

}
