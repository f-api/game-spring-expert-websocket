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

    public void broadcastRaw(
            Long roomId,
            String json
    ) {
        for (WebSocketSession session : registry.sessions(roomId)) {
            sendRaw(session, json);
        }
    }

    public void sendTo(
            WebSocketSession session,
            Object message
    ) {
        String json = objectMapper.writeValueAsString(message);
        sendRaw(session, json);
    }

    private void sendRaw(
            WebSocketSession session,
            String json
    ) {
        try {
            synchronized (session) {
                if (session.isOpen()) {
                    TextMessage message = new TextMessage(json);
                    session.sendMessage(message);
                }
            }
        } catch (Exception sendFailed) {
            log.warn("전송 실패: id={}", session.getId());
        }
    }
}
