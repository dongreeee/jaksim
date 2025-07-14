package com.hazel.jaksim.monthlygoal;

import com.hazel.jaksim.todo.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MonthlyGoalController {

    private final MonthlyGoalRepository monthlyGoalRepository;

    @GetMapping("/monthlyGoal/addView")
    public String addView(Authentication auth){
        return "monthlyGoal_add.html";
    }

    @GetMapping("/monthlyGoal/info/{month}")
    public ResponseEntity<?> getMonthlyByDate(@PathVariable String month, Authentication auth) {
        String username = auth.getName();
        return monthlyGoalRepository.findByUsernameAndDateYm(username, month)
                .map(goal -> ResponseEntity.ok(goal))
                .orElse(ResponseEntity.noContent().build());  // 204 응답
    }




    @PostMapping("/monthlyGoal/add")
    ResponseEntity<String> todoCheck(@RequestBody Map<String, String> payload,
                                     Authentication auth){

        String title = payload.get("title");
        String dateYm = payload.get("dateym");
        String username = auth.getName();

        MonthlyGoal monthlyGoal = new MonthlyGoal();
        monthlyGoal.setUsername(username);
        monthlyGoal.setTitle(title);
        monthlyGoal.setDateYm(dateYm);
        monthlyGoalRepository.save(monthlyGoal);

        return ResponseEntity.ok().build();
    }

}
