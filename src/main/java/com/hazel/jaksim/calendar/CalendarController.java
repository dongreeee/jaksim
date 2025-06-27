package com.hazel.jaksim.calendar;

import com.hazel.jaksim.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
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

    @GetMapping("/calendaredit/{date}")
    public String calendaredit(@PathVariable String date, Model model ){
        model.addAttribute("date",date);
        return "calendar_add.html";
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

}
