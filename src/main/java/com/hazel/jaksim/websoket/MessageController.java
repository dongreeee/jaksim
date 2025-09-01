package com.hazel.jaksim.websoket;

import com.hazel.jaksim.websoket.dto.MessageDto;
import com.hazel.jaksim.websoket.dto.ShareMsgDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageRepository messageRepository;

    private MessageService messageService;

    @PostMapping("/share")
    public ResponseEntity<String> share(@RequestBody ShareMsgDto dto, Authentication auth) {
        return messageService.shareCalendar(dto, auth.getName());
    }




    @GetMapping("/notications/messages")
    public ResponseEntity<List<MessageDto>> getMessages(Authentication auth) {
        String username = auth.getName();  // 로그인한 유저 아이디
        List<MessageDto> messages = messageRepository.findMessageDtosByRecieveUser(username);
        return ResponseEntity.ok(messages);
    } // 꼭 리스트로!



    @GetMapping("/notications/count")
    public ResponseEntity<Long> getUnreadCount(Authentication auth){
        String username = auth.getName();
        long count = messageRepository.countByRecieveUserAndVc(username, "0");
        return ResponseEntity.ok(count);
    }

//    알림을 클릭했을 때 그 알림의 VC 값을 1로 변경해주는 api
    @PostMapping("/messages/read-chk")
    public ResponseEntity<Long> messageReadChk(@RequestBody Map<String, Long> payload,
                                                Authentication auth){
        //      요청 바디에서 messageId 추출
        Long messageId = payload.get("messageId");
        //       db에서 헤당 메시지 조회
        Message message = messageRepository.findById(messageId).orElseThrow();
        //       접근 권한 검사 : 이 메세지를 받은 사용자가 현재 로그인한 사용자와 같은지 확인
        //       같지 않으면 403 forbidden 에러를 반환해서 차단함
        //       -> 보안 필수! 다른 사람 메시지를 조작 못하게 막음
        if (!message.getRecieveUser().equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        message.setVc("1");
        message.setRecieveReg(LocalDateTime.now());
        messageRepository.save(message);
        return ResponseEntity.ok().build();
    }



}