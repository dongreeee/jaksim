package com.hazel.jaksim.calendar;

import com.hazel.jaksim.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarRepository calendarRepository;

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
        Calendar calendar = new Calendar();
        Optional<Calendar> result = calendarRepository.findById(id);
        model.addAttribute("calendar",result.get());
        return "calendar_edit.html";
    }

    @PostMapping("/addCalender")
    public String addCalender(String title,
                              String title_color,
                              String content,
                              String sdate,
                              String edate,
                              Authentication auth){
        try{
            Calendar calendar = new Calendar();
            calendar.setTitle(title);
            calendar.setTitle_color(title_color);
            calendar.setContent(content);
            calendar.setSdate(sdate);
            calendar.setEdate(edate);
            calendar.setUsername(auth.getName());

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

}
