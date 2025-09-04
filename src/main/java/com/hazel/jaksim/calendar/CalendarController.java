package com.hazel.jaksim.calendar;

import com.hazel.jaksim.calendar.dto.*;
import com.hazel.jaksim.map.dto.PlaceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarRepository calendarRepository;
    private final CalendarService calendarService;
    private final S3Service s3Service;
//    캘린더 view
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

        CalendarResponse dto = calendarService.getEditCalendar(id);
        model.addAttribute("dto", dto);

        return "calendar_edit.html";
    }

    @PostMapping("/addCalendar")
    public String addCalender(@RequestParam String places,
                              @ModelAttribute CalendarAddDto formDto,
                              Authentication auth){
        try{

            ObjectMapper mapper = new ObjectMapper();
            List<PlaceDto> placesJson = mapper.readValue(places, new TypeReference<List<PlaceDto>>() {});
            calendarService.addCalendar(placesJson, formDto, auth.getName());
            return "redirect:/calendar";
        }
        catch (Exception e){
            e.printStackTrace();
            return "calendar";

        }
    }

    @PostMapping("/editCalender")
    public String editCalender(@ModelAttribute CalendarUpdateDto dto){
        try{
            calendarService.updateCalendar(dto);
            return "redirect:/calendar";
        }
        catch (Exception e){
            return "calendar";
        }
    }

    @DeleteMapping("/deleteCalendar")
    ResponseEntity<String> DeleteItem(@RequestBody Calendar body){
        calendarRepository.deleteById(body.getId());
        return ResponseEntity.status(200).body("삭제완료");
    }



    @GetMapping("/shareCalendarView/{messageId}/{calendarId}")
    public String viewSharedCalendar(@PathVariable Long messageId,
                                     @PathVariable Long calendarId,
                                     Authentication auth,
                                     Model model){

        CalendarViewDataDto viewData = calendarService.getShareCalendar(messageId, calendarId);

        model.addAttribute("dto", viewData.getDto());
        model.addAttribute("msgId", viewData.getMsgId());
        model.addAttribute("sharedChk", viewData.isSharedChk());

        return "calendar_share.html";
    }

    @PostMapping("/sharedCalenderAdd")
    public String sharedCalenderAdd(@ModelAttribute SharedCalendarAddDto formDto,
                              Authentication auth){
        try{
            calendarService.shareCalendarAdd(formDto, auth.getName());
            return "redirect:/calendar";
        }
        catch (Exception e){
            return "calendar";
        }
    }

    @GetMapping("/calendar/navInfo")
    @ResponseBody
    public List<Calendar> getTodosByDate(Authentication auth) {
        String username = auth.getName();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return calendarService.getTodosByDate(today , username);
    }

    @GetMapping("presigned-url")
    @ResponseBody
    String getURL(@RequestParam String filename){
        var result = s3Service.createPresignedUrl("test/" +filename);
        System.out.println(result);
        return result;
    }

}
