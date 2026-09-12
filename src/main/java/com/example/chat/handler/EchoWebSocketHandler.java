package com.example.chat.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class EchoWebSocketHandler extends TextWebSocketHandler {
    @Override
    public void afterConnectionEstablished(
            WebSocketSession session
    ) {
        log.info("연결됨: id={}", session.getId());
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message
    ) throws Exception {
        log.info("받음: {}", message.getPayload());
        TextMessage echo = new TextMessage("echo: " + message.getPayload());
        session.sendMessage(echo);
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status
    ) {
        log.info("끊김: id={} code={}", session.getId(), status.getCode());
    }
}
