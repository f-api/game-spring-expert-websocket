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
    private final ChatRelay relay;
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
        String content = message.required("content").requireNonNull().stringValue();
        if (content.isBlank() || content.length() > 200) {
            throw new IllegalArgumentException("content 는 1~200자입니다");
        }
        ChatResponse saved = chatService.save(roomId, nickname, content);
        String payload = objectMapper.writeValueAsString(saved);
        relay.publish(roomId, payload);
    }
}
