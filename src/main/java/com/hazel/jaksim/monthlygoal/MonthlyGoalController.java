package com.hazel.jaksim.monthlygoal;

import com.hazel.jaksim.calendar.Calendar;
import com.hazel.jaksim.todo.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MonthlyGoalController {

    private final MonthlyGoalRepository monthlyGoalRepository;
    private final MonthlyGoalContentRepository monthlyGoalContentRepository;

    @GetMapping("/monthlyGoal/addView")
    public String addView(Authentication auth, Model model){
        LocalDateTime now = LocalDateTime.now();
        String ym = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String ymd = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        model.addAttribute("month", ym);
        model.addAttribute("date", ymd);
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

        System.out.println("title:" + title);

        MonthlyGoal monthlyGoal = new MonthlyGoal();
        monthlyGoal.setUsername(username);
        monthlyGoal.setTitle(title);
        monthlyGoal.setDateYm(dateYm);
        monthlyGoalRepository.save(monthlyGoal);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/monthlyGoal/addContent")
    public String addGoalContent(String date,
                                 String content,
                                 String goal_id,
                                 Authentication auth){
        try{
            Optional<MonthlyGoal> monthlyGoalOptional = monthlyGoalRepository.findById(Long.valueOf(goal_id));

            MonthlyGoalContent monthlyGoalContent = new MonthlyGoalContent();
            monthlyGoalContent.setDateYmd(date);
            monthlyGoalContent.setMonthlyGoal(monthlyGoalOptional.get());
            monthlyGoalContent.setContent(content);
            monthlyGoalContent.setUsername(auth.getName());

            monthlyGoalContentRepository.save(monthlyGoalContent);
            return "redirect:/monthlyGoal/addView";
        }
        catch (Exception e){

            return "monthlyGoal/addView";

        }
    }


    @GetMapping("/monthlyGoal/goalCount")
    @ResponseBody
    public List<String> goalCount(Authentication auth){
        String username = auth.getName();
//      java 8이상에서 사용하는 Stream API 구문 , DB 에서 가져온 리스트를 가공해서 반환하는 코드

        return monthlyGoalContentRepository.findByUsername(username).stream()
//                db에서 해당 유저의 목표 달성 리스트 가져 온 후 Stream으로 변환
//                -> 이후 데이터를 가공(map) , 필터 (filter), 수(collect) 등의 작업을 하기 위해 stream을 사용
//                내부적으로 각 요소를 순회하면서 goal이라는 이름으로 받아서 처리한다.
                .map(goal -> goal.getDateYmd().toString())  // "yyyy-MM-dd"
//                MonthlyGoalCount 객체에서 날짜 데이터를 꺼내서 .toString()으로 변환
                .collect(Collectors.toList());
//                위에서 만든 문자열 스트림을 다시 리스트로 모아줌 -> List<String> return
//              - username에 해당하는 목표들의 날짜만 문자열로 추출해서 리스트로 반환하는 것
    }


}
