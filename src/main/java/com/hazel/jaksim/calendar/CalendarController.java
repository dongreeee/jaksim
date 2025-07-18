package com.hazel.jaksim.calendar;

import com.hazel.jaksim.calendar.dto.CalendarFormDto;
import com.hazel.jaksim.calendar.dto.CalendarResponse;
import com.hazel.jaksim.map.MapRepository;
import com.hazel.jaksim.map.MapService;
import com.hazel.jaksim.member.Member;
import com.hazel.jaksim.member.MemberRepository;
import com.hazel.jaksim.websoket.Message;
import com.hazel.jaksim.websoket.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarRepository calendarRepository;
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final MapRepository mapRepository;
    private final SimpMessagingTemplate messagingTemplate;


    private final CalendarService calendarService;
    private final MapService mapService;

    @GetMapping("/calendar")
    public String calendar(Model model, Authentication auth){
        List<Calendar>results = calendarRepository.findByUsername(auth.getName());
        model.addAttribute("events", results);

        return  "calendar.html";
    }

    @GetMapping("/addCalendarView/{date}")
    public String addCalendar(@PathVariable String date, Model model ){
        model.addAttribute("date",date);
        return "calendar_add.html";
    }

    @GetMapping("/editCalendarView/{id}")
    public String editCalendar(@PathVariable Long id, Model model ){
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

        model.addAttribute("dto", dto);

        System.out.println(dto.toString());
        return "calendar_edit.html";
    }

    @PostMapping("/addCalender")
    public String addCalender(@ModelAttribute CalendarFormDto formDto,
                              Authentication auth){
        Long map_id;
        Optional<com.hazel.jaksim.map.Map>map = Optional.empty();
        try{
            System.out.println(formDto.toString());
             if(formDto.isMapChk()){
                 map = mapService.addMap(formDto);
             }

            Calendar calendar = new Calendar();
            calendar.setTitle(formDto.getTitle());
            calendar.setTitle_color(formDto.getTitleColor());
            calendar.setContent(formDto.getContent());
            calendar.setSdate(formDto.getSdate());
            calendar.setEdate(formDto.getEdate());
            calendar.setUsername(auth.getName());

            if(map.isPresent()){
                calendar.setMap(map.get());
            }

            calendarRepository.save(calendar);
            return "redirect:/calendar";
        }
        catch (Exception e){

            return "calendar";

        }
    }

    @PostMapping("/editCalender")
    public String editCalender(@RequestParam Map<String, Object> formData,
                              Authentication auth){
        System.out.println("id:"+Long.parseLong((String) formData.get("id")));
        Optional<Calendar>optionalCal = calendarRepository.findById(Long.parseLong((String) formData.get("id")));
        //String → Long 캐스팅 불가 → ClassCastException 자동 형변환 x

        if(!optionalCal.isPresent()){
            throw new IllegalArgumentException("id 조회 x");
        }


        try{
            Calendar calendar = optionalCal.get();
            calendar.setTitle((String) formData.get("title"));
            calendar.setTitle_color((String) formData.get("title_color"));
            calendar.setContent((String) formData.get("content"));
            calendar.setSdate((String) formData.get("sdate"));
            calendar.setEdate((String) formData.get("edate"));

            calendarRepository.save(calendar);
            return "redirect:/calendar";
        }
        catch (Exception e){

            return "calendar";

        }
    }

    @DeleteMapping("/deleteCalendar")
    ResponseEntity<String> DeleteItem(@RequestBody Calendar body){
        System.out.println(body.getId());
        calendarRepository.deleteById(body.getId());
        return ResponseEntity.status(200).body("삭제완료");
    }

    @PostMapping("/share")
    public ResponseEntity<String> share(@RequestBody Map<String, String> payload, Authentication auth) {
        Optional<Member> member = memberRepository.findByUsername(payload.get("username"));
        Optional<Calendar> calendar = calendarRepository.findById(Long.valueOf(payload.get("calendar_id")));

        if (!member.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 아이디입니다.");
        }

        if (!calendar.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("캘린더 조회 실패");
        }

        String sendUser = auth.getName();
        String recieveUser = payload.get("username");

        if (sendUser.equals(recieveUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("자기 자신에게 공유할 수 없습니다.");
        }


        try {
            Message message = new Message();
            message.setSendUser(member.get());
            message.setRecieveUser(recieveUser);
            message.setSendReg(LocalDateTime.now());
            message.setCalendar(calendar.get());

            messageRepository.save(message);

            messagingTemplate.convertAndSend("/topic/notify/" + recieveUser, "공유가 완료되었습니다!");

            return ResponseEntity.ok("공유완료!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("DB 저장 실패");
        }
    }


    @GetMapping("/shareCalendarView/{calendarId}")
    public String viewSharedCalendar(@PathVariable Long calendarId,
                                     Authentication auth,
                                     Model model){

        Optional<Calendar> calendar = calendarRepository.findById(calendarId);
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
        dto.setCalendarId(calendarId);
        dto.setTitle(calendar.get().getTitle());
        dto.setTitleColor(calendar.get().getTitle_color());
        dto.setContent(calendar.get().getContent());
        dto.setSdate(calendar.get().getSdate());
        dto.setEdate(calendar.get().getEdate());

        if(calendar.get().getMap() != null){
            map = mapRepository.findById(calendar.get().getMap().getId());
            dto.setMapId(map.get().getId());
            dto.setSelectedPlaceName(map.get().getPlaceName());
            dto.setSelectedPlaceAddress(map.get().getPlaceAddress());
            dto.setSelectedPlaceLat(map.get().getPlaceX());
            dto.setSelectedPlaceLng(map.get().getPlaceY());
            dto.setSelectedPlaceUrl(map.get().getPlaceUrl());
            isMapChk = true;
        }

        dto.setMapChk(isMapChk);

        model.addAttribute("dto", dto);



        return "calendar_share.html";
    }

    @GetMapping("/calendar/navInfo")
    @ResponseBody
    public List<Calendar> getTodosByDate(Authentication auth) {
        String username = auth.getName();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return calendarService.getTodosByDate(today , username);
    }

}
