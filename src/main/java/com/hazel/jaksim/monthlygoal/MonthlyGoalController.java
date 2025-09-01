package com.hazel.jaksim.monthlygoal;

import com.hazel.jaksim.common.DateUtil;
import com.hazel.jaksim.monthlygoal.dto.MonthlyGoalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class MonthlyGoalController {

    private final MonthlyGoalService monthlyGoalService;

    @GetMapping("/monthlyGoal/addView")
    public String addView(Authentication auth, Model model){
        String ym = DateUtil.getCurrentYearMonth();
        String ymd = DateUtil.getCurrentDate();

        boolean isTodayChk = monthlyGoalService.monthlyGoalToday(auth.getName(), ymd);

        model.addAttribute("month", ym);
        model.addAttribute("date", ymd);
        model.addAttribute("today", isTodayChk);
        return "monthlyGoal_add.html";
    }

    @GetMapping("/monthlyGoal/info/{month}")
    public ResponseEntity<?> getMonthlyByDate(@PathVariable String month, Authentication auth) {
        return monthlyGoalService.getMonthlyByDate(month, auth.getName());
    }

    @PostMapping("/monthlyGoal/add")
    public ResponseEntity<String> todoCheck(@RequestBody Map<String, String> payload,
                                     Authentication auth){

        String title = payload.get("title");
        String dateYm = payload.get("dateym");
        String username = auth.getName();

        return monthlyGoalService.todoCheckAdd(title, dateYm, username);
    }

    @PostMapping("/monthlyGoal/addContent")
    public String addGoalContent(String date,
                                 String content,
                                 String goal_id,
                                 Authentication auth){
        try{
            monthlyGoalService.addGoalContent(date, content, goal_id, auth.getName());
            return "redirect:/monthlyGoal/addView";
        }
        catch (Exception e){
            return "monthlyGoal/addView";
        }
    }


    @GetMapping("/monthlyGoal/goalCount")
    @ResponseBody
    public MonthlyGoalResponse goalCount(Authentication auth){
       return monthlyGoalService.goalCount(auth.getName());
    }


}
