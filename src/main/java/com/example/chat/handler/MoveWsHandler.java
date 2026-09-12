package com.example.chat.handler;

import com.example.chat.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.JsonNode;

@Component
@RequiredArgsConstructor
public class MoveWsHandler implements WsMessageHandler {
    private final PositionService positionService;

    @Override
    public String type() {
        return "move";
    }

    @Override
    public void handle(
            WebSocketSession session,
            Long roomId,
            String nickname,
            JsonNode message
    ) {
        double x = message.required("x").doubleValue();
        double y = message.required("y").doubleValue();
        positionService.update(roomId, nickname, x, y);
    }
}
