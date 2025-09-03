package com.hazel.jaksim.calendar;

import com.hazel.jaksim.calendar.dto.*;
import com.hazel.jaksim.map.Map;
import com.hazel.jaksim.map.MapRepository;
import com.hazel.jaksim.map.MapService;
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
    private final MapRepository mapRepository;
    private final MessageRepository messageRepository;

    private final MapService mapService;

    public CalendarResponse getEditCalendar(Long id){
        Optional<Calendar> calendar = calendarRepository.findById(id);
        Optional<com.hazel.jaksim.map.Map> map = Optional.empty();
        Boolean isMapChk = false;


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
        dto.setCalendarId(id);
        dto.setTitle(calendar.get().getTitle());
        dto.setTitleColor(calendar.get().getTitle_color());
        dto.setContent(calendar.get().getContent());
        dto.setSdate(calendar.get().getSdate());
        dto.setEdate(calendar.get().getEdate());
        dto.setFileName(calendar.get().getImgUrl());
        if(calendar.get().getMap() != null){
            map = mapRepository.findById(calendar.get().getMap().getId());
            dto.setSelectedPlaceName(map.get().getPlaceName());
            dto.setSelectedPlaceAddress(map.get().getPlaceAddress());
            dto.setSelectedPlaceLat(map.get().getPlaceX());
            dto.setSelectedPlaceLng(map.get().getPlaceY());
            dto.setSelectedPlaceUrl(map.get().getPlaceUrl());
            isMapChk = true;
        }

        dto.setMapChk(isMapChk);

        return dto;
    }

    public void addCalendar(CalendarAddDto formDto, String username){
        Optional<com.hazel.jaksim.map.Map>map = Optional.empty();

        if (Boolean.TRUE.equals(formDto.getMapChk())) {
            // 위도/경도 필드가 모두 존재할 때만 지도 저장 시도
            if (formDto.getSelectedPlaceLat() != null && formDto.getSelectedPlaceLng() != null) {
                map = mapService.addMap(formDto);
            }
        }

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
        map.ifPresent(calendar::setMap);

        calendarRepository.save(calendar);
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
        Optional<com.hazel.jaksim.map.Map> map = Optional.empty();
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

        if (calendar.getMap() != null) {
            map = mapRepository.findById(calendar.getMap().getId());
            map.ifPresent(m -> {
                dto.setMapId(m.getId());
                dto.setSelectedPlaceName(m.getPlaceName());
                dto.setSelectedPlaceAddress(m.getPlaceAddress());
                dto.setSelectedPlaceLat(m.getPlaceX());
                dto.setSelectedPlaceLng(m.getPlaceY());
                dto.setSelectedPlaceUrl(m.getPlaceUrl());
            });
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

    public void shareCalendarAdd(SharedCalendarAddDto formDto, String username){
        com.hazel.jaksim.map.Map map = null;

        if (formDto.getMapId() != null) {
            map = mapRepository.findById(formDto.getMapId()).orElse(null);
        }

        Calendar calendar = new Calendar();
        calendar.setTitle(formDto.getTitle());
        calendar.setTitle_color(formDto.getTitleColor());
        calendar.setContent(formDto.getContent());
        calendar.setSdate(formDto.getSdate());
        calendar.setEdate(formDto.getEdate());
        calendar.setUsername(username);
        calendar.setMap(map);

        System.out.println(formDto.getMessageId());
        calendarRepository.save(calendar);
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
