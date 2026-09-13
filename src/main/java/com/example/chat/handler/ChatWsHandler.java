package com.example.chat.handler;

import com.example.chat.dto.ChatResponse;
import com.example.chat.service.ChatRelay;
import com.example.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class ChatWsHandler implements WsMessageHandler {
    private final ChatService chatService;
    private final ChatRelay chatRelay;
    private final ObjectMapper objectMapper;

    @Override
    public String type() {
        return "chat";
    }

    @Override
    public void handle(
            WebSocketSession session,
            Long roomId,
            String nickname,
            JsonNode message
    ) {
        if (!message.path("content").isString()) {
            return;
        }
        String content = message.path("content").asString("");
        if (content.isBlank() || content.length() > 200) {
            return;
        }
        ChatResponse savedChat = chatService.save(roomId, nickname, content);
        String jsonMessage = objectMapper.writeValueAsString(savedChat);
        chatRelay.publish(roomId, jsonMessage);
    }
}
