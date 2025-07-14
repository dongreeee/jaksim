package com.hazel.jaksim.calendar;

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
    private final CalendarService calendarService;
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

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


    @GetMapping("/sharedCalendarView/{messageId}")
    public String viewSharedCalendar(@PathVariable Long messageId,
                                     Authentication auth,
                                     Model model){

        // 메세지 조회
        Optional<Message> message = messageRepository.findById(messageId);

        // 로그인 유저가 받은 사람인지 확인
        if(!message.get().getRecieveUser().equals(auth.getName())){
//            공유받을 사용자와 로그인한 사용자가 일치하지 않으면 ???
            model.addAttribute("error", "false");
            return "calendar";
        }

        // 공유받은 캘린더 조회
        Calendar calendar = message.get().getCalendar();
        if(calendar == null){
            return  "calendar";
        }

        model.addAttribute("calendar", message.get().getCalendar());
        model.addAttribute("shared", true);

        return "calendar_edit.html";
    }

    @GetMapping("/calendar/navInfo")
    @ResponseBody
    public List<Calendar> getTodosByDate(Authentication auth) {
        String username = auth.getName();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return calendarService.getTodosByDate(today , username);
    }

}
