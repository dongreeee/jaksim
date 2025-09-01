package com.hazel.jaksim.monthlygoal;

import com.hazel.jaksim.monthlygoal.dto.MonthlyGoalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonthlyGoalService {

    private final MonthlyGoalRepository monthlyGoalRepository;
    private final MonthlyGoalContentRepository monthlyGoalContentRepository;

    public boolean monthlyGoalToday(String username, String ymd){
        boolean isTodayChk = false;

        Optional<MonthlyGoalContent> monthlyGoalContent = monthlyGoalContentRepository.findByUsernameAndDateYmd(username, ymd);

        if(monthlyGoalContent.isPresent()){
            isTodayChk = true;
        }

        return isTodayChk;
    }

    public ResponseEntity<?> getMonthlyByDate(String month, String username) {
        return monthlyGoalRepository.findByUsernameAndDateYm(username, month)
                .map(goal -> ResponseEntity.ok(goal))
                .orElse(ResponseEntity.noContent().build());  // 204 응답
    }

    public ResponseEntity<String> todoCheckAdd(String title,
                                        String dateYm,
                                        String username){

        MonthlyGoal monthlyGoal = new MonthlyGoal();
        monthlyGoal.setUsername(username);
        monthlyGoal.setTitle(title);
        monthlyGoal.setDateYm(dateYm);
        monthlyGoalRepository.save(monthlyGoal);

        return ResponseEntity.ok().build();
    }

    public void addGoalContent(String date,
                               String content,
                               String goal_id,
                               String username){
        Optional<MonthlyGoal> monthlyGoalOptional = monthlyGoalRepository.findById(Long.valueOf(goal_id));

        MonthlyGoalContent monthlyGoalContent = new MonthlyGoalContent();
        monthlyGoalContent.setDateYmd(date);
        monthlyGoalContent.setMonthlyGoal(monthlyGoalOptional.get());
        monthlyGoalContent.setContent(content);
        monthlyGoalContent.setUsername(username);

        monthlyGoalContentRepository.save(monthlyGoalContent);
    }


    public MonthlyGoalResponse goalCount(String username){

//      java 8이상에서 사용하는 Stream API 구문 , DB 에서 가져온 리스트를 가공해서 반환하는 코드
        List<String> goalDates = monthlyGoalContentRepository.findByUsername(username).stream()
                //                db에서 해당 유저의 목표 달성 리스트 가져 온 후 Stream으로 변환
//                -> 이후 데이터를 가공(map) , 필터 (filter), 수(collect) 등의 작업을 하기 위해 stream을 사용
//                내부적으로 각 요소를 순회하면서 goal이라는 이름으로 받아서 처리한다.
                .map(goal -> goal.getDateYmd().toString())
                //                MonthlyGoalCount 객체에서 날짜 데이터를 꺼내서 .toString()으로 변환
                .collect(Collectors.toList());
//        위에서 만든 문자열 스트림을 다시 리스트로 모아줌 -> List<String> return
//              - username에 해당하는 목표들의 날짜만 문자열로 추출해서 리스트로 반환하는 것

        // yyyy-MM 형태의 목표 달 리스트
        Set<String> allowedMonthSet = monthlyGoalRepository.findByUsername(username).stream()
                .map(goal -> goal.getDateYm().toString())
                .collect(Collectors.toSet()); // Set으로 중복 제거

        // 이번 달 추가 (중복되면 자동 제거됨)
        String currentYm = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        allowedMonthSet.add(currentYm);

        // Set을 다시 List로 변환 (필요하면 정렬도 가능)
        List<String> allowedMonths = new ArrayList<>(allowedMonthSet);
        Collections.sort(allowedMonths); // 선택 사항: 달 오름차순 정렬

        return new MonthlyGoalResponse(allowedMonths, goalDates);
    }

}
