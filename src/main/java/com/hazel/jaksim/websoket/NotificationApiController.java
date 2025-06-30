package com.hazel.jaksim.websoket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationApiController {

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/api/notify")
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationRequest dto) {
        messagingTemplate.convertAndSend("/topic/notify/" + dto.getTargetUsername(), dto.getMessage());
        return ResponseEntity.ok().build();
    }

    @Getter
    @Setter
    public static class NotificationRequest {
        private String targetUsername;
        private String message;
    }
}
