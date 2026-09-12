package com.example.chat.handler;

import tools.jackson.databind.JsonNode;
import org.springframework.web.socket.WebSocketSession;

public interface WsMessageHandler {
    String type();

    void handle(
            WebSocketSession session,
            Long roomId,
            String nickname,
            JsonNode message
    );
}
