package com.hazel.jaksim.calendar;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CalendarController {

    @GetMapping("/calendaredit")
    public String calendaredit(){

        return "calendar_add.html";
    }

}
