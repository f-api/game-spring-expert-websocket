package com.example.chat.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomBroadcaster {
    private final RoomSessionRegistry registry;
    private final ObjectMapper objectMapper;

    public void broadcastJson(
            Long roomId,
            String jsonMessage
    ) {
        for (WebSocketSession session : registry.sessions(roomId)) {
            sendJson(session, jsonMessage);
        }
    }

    public void sendTo(
            WebSocketSession session,
            Object message
    ) {
        String jsonMessage = objectMapper.writeValueAsString(message);
        sendJson(session, jsonMessage);
    }

    private void sendJson(
            WebSocketSession session,
            String jsonMessage
    ) {
        try {
            synchronized (session) {
                if (session.isOpen()) {
                    TextMessage message = new TextMessage(jsonMessage);
                    session.sendMessage(message);
                }
            }
        } catch (Exception sendFailed) {
            log.warn("전송 실패: id={}", session.getId());
        }
    }
}
