package com.hazel.jaksim.websoket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
// websocket 사용을 활성화 하고 @Configuration 어노테이션을 통해 환경파일임을 지정
// 이 어노테이션을 사용하면 Spring 애플리케이션에서 Websocket 기능을 사용할 수 있다 .
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
// WebsocketConfigurer 인터페이스로부터 구현체 registerWebsocketHandlers메서드 구성
// 이 인터페이스를 구현하면 registerWebSocketHandlers 메서드를 통해 websocket 핸들러를 등록할 수 있다.
    private final WebsocketHandler websocketHandler;

    /**
     * WebSocket 연결을 위해서 Handler를 구성합니다.
     *
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//  WebSocketConfigurer 인터페이스로부터 오버라이딩 받은 WebSocket 핸들러를 구성합니다.
//  클라이언트에서 /ws-stomp경로로 websocket 연결을 시도하면 websokethandler으로 연결을 처리하게 핸들러를 등록한다.
        System.out.println("[+] 최초 WebSocket 연결을 위한 등록 Handler");
        registry
                // 클라이언트에서 웹 소켓 연결을 위해 "ws-stomp"라는 엔드포인트로 연결을 시도하면 ChatWebSocketHandler 클래스에서 이를 처리합니다.
                .addHandler(websocketHandler, "ws-stomp")
                // 접속 시도하는 모든 도메인 또는 IP에서 WebSocket 연결을 허용합니다.
                .setAllowedOrigins("*");
    }

}