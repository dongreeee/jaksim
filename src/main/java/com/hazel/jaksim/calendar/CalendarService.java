package com.hazel.jaksim.calendar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hazel.jaksim.calendar.dto.*;
import com.hazel.jaksim.map.*;
import com.hazel.jaksim.map.dto.PlaceDto;
import com.hazel.jaksim.map.dto.PlaceMapDto;
import com.hazel.jaksim.websoket.Message;
import com.hazel.jaksim.websoket.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final MapInfoRepository mapInfoRepository;
    private final CalendarMapRepository calendarMapRepository;
    private final MessageRepository messageRepository;

    private final MapService mapService;

    public CalendarResponse getEditCalendar(Long id){
        Optional<Calendar> calendar = calendarRepository.findById(id);
        List<CalendarMap> calendarMap = calendarMapRepository.findByCalendarId(id);
        Optional<MapInfo> map = Optional.empty();
        Boolean isMapChk = false;


        CalendarResponse dto = new CalendarResponse();
        dto.setCalendarId(id);
        dto.setTitle(calendar.get().getTitle());
        dto.setTitleColor(calendar.get().getTitle_color());
        dto.setContent(calendar.get().getContent());
        dto.setSdate(calendar.get().getSdate());
        dto.setEdate(calendar.get().getEdate());
        dto.setFileName(calendar.get().getImgUrl());
        if(!calendarMap.isEmpty()){
            isMapChk = true;
        }

        dto.setMapChk(isMapChk);

        return dto;
    }

    public void addCalendar(List<PlaceDto>placeDtos, CalendarAddDto formDto, String username) throws JsonProcessingException {

        Calendar calendar = new Calendar();
        calendar.setTitle(formDto.getTitle());
        calendar.setTitle_color(formDto.getTitleColor());
        calendar.setContent(formDto.getContent());
        calendar.setSdate(formDto.getSdate());
        calendar.setEdate(formDto.getEdate());
        calendar.setUsername(username);

        if(formDto.getFilename() != null && !formDto.getFilename().isEmpty()){
            calendar.setImgUrl(formDto.getFilename());
        }

//        map에 값이 있으면 calendar.setMap()을 호출해서 Calendar 객체에 지도 정보를 설정하고,
//        값이 없으면 아무 것도 하지 않는다.
//        if (map.isPresent()) { // Optional 안에 값이 있으면
//            calendar.setMap(map.get()); // 그 값을 calendar.setMap()에 넣어줌
//        }
        // map이 존재하면 저장
//        map.ifPresent(calendar::setMapInfo);

        calendarRepository.save(calendar);

        if (placeDtos != null) {

            for (PlaceDto placeDto : placeDtos) {  // ✅ places는 이제 method parameter로 전달
                MapInfo mapInfo = mapService.addMap(placeDto);
                mapService.addCalendarMap(placeDto.getNo(), calendar, mapInfo);
            }
        }

    }

    public void updateCalendar(CalendarUpdateDto dto) {
        Optional<Calendar> optionalCal = calendarRepository.findById(dto.getId());

        if(!optionalCal.isPresent()){
            throw new IllegalArgumentException("id 조회 x");
        }

        Calendar calendar = optionalCal.get();
        calendar.setTitle(dto.getTitle());
        calendar.setTitle_color(dto.getTitle_color());
        calendar.setContent(dto.getContent());
        calendar.setSdate(dto.getSdate());
        calendar.setEdate(dto.getEdate());

        calendarRepository.save(calendar);

    }




    public CalendarViewDataDto getShareCalendar(Long messageId, Long calendarId){



        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(()-> new RuntimeException("Calendar not found"));

        Optional<Message> message = messageRepository.findById(messageId);
        Optional<MapInfo> map = Optional.empty();
        List<CalendarMap> calendarMap = calendarMapRepository.findByCalendarId(calendarId);
        Boolean isMapChk = false;
        Boolean isSharedChk = false;


//        private Long CalendarId;
//        private String titleColor;
//        private String title;
//        private String content;
//        private String sdate;
//        private String edate;
//        private String selectedPlaceName;
//        private String selectedPlaceAddress;
//        private double selectedPlaceLat;
//        private double selectedPlaceLng;
//        private String selectedPlaceUrl;

        CalendarResponse dto = new CalendarResponse();
        dto.setCalendarId(calendarId);
        dto.setTitle(calendar.getTitle());
        dto.setTitleColor(calendar.getTitle_color());
        dto.setContent(calendar.getContent());
        dto.setSdate(calendar.getSdate());
        dto.setEdate(calendar.getEdate());
        dto.setFileName(calendar.getImgUrl());

        if(!calendarMap.isEmpty()){
            isMapChk = true;
        }

        dto.setMapChk(isMapChk);

        if (message.isPresent()) {
            if ("1".equals(message.get().getIsShared())) {
                isSharedChk = true;
            }
        }

        return new CalendarViewDataDto(dto, messageId, isSharedChk);
    }

    public void shareCalendarAdd(List<PlaceMapDto> placeDtos, SharedCalendarAddDto formDto, String username){
        System.out.println(placeDtos);
        System.out.println(formDto);

        Calendar calendar = new Calendar();
        calendar.setTitle(formDto.getTitle());
        calendar.setTitle_color(formDto.getTitleColor());
        calendar.setContent(formDto.getContent());
        calendar.setSdate(formDto.getSdate());
        calendar.setEdate(formDto.getEdate());
        calendar.setUsername(username);


        if(formDto.getFileName() != null && !formDto.getFileName().isEmpty()){
            calendar.setImgUrl(formDto.getFileName());
        }


        System.out.println(formDto.getMessageId());
        calendarRepository.save(calendar);

        if (placeDtos != null) {

            for (PlaceMapDto placeDto : placeDtos) {  // ✅ places는 이제 method parameter로 전달
                Optional<MapInfo> mapInfo = mapInfoRepository.findById(placeDto.getMapId());
                mapService.addCalendarMap(placeDto.getNo(), calendar, mapInfo.orElse(null));
            }
        }


        Optional<Message> optionalMessage = messageRepository.findById(formDto.getMessageId());
        if(optionalMessage.isPresent()){
            System.out.println("message Update");
            Message message = optionalMessage.get();
            message.setIsShared("1");
            message.setIsSharedReg(LocalDateTime.now());
            messageRepository.save(message);
        }

    }



    public List<Calendar> getTodosByDate(String today, String username) {
        return calendarRepository.findByDateBetween(today, username);
    }
}
