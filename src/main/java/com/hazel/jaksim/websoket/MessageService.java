package com.hazel.jaksim.websoket;

import com.hazel.jaksim.calendar.Calendar;
import com.hazel.jaksim.calendar.CalendarRepository;
import com.hazel.jaksim.member.Member;
import com.hazel.jaksim.member.MemberRepository;
import com.hazel.jaksim.websoket.dto.ShareMsgDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MemberRepository memberRepository;
    private final CalendarRepository calendarRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ResponseEntity<String> shareCalendar(ShareMsgDto dto, String sender) {
        System.out.println(dto.getCalendarId()+dto.getUsername());
        Optional<Member> member = memberRepository.findByUsername(dto.getUsername());
        Optional<Calendar> calendar = calendarRepository.findById(dto.getCalendarId());

        if (!member.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 아이디입니다.");
        }

        if (!calendar.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("캘린더 조회 실패");
        }


        if (sender.equals(dto.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("자기 자신에게 공유할 수 없습니다.");
        }


        try {
            Message message = new Message();
            message.setSendUser(member.get());
            message.setRecieveUser(dto.getUsername());
            message.setSendReg(LocalDateTime.now());
            message.setCalendar(calendar.get());

            messageRepository.save(message);

            messagingTemplate.convertAndSend("/topic/notify/" + dto.getUsername(), "공유가 완료되었습니다!");

            return ResponseEntity.ok("공유완료!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("DB 저장 실패");
        }

    }
}
