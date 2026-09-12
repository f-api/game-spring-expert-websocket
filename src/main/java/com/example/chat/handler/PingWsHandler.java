package com.example.chat.handler;

import com.example.chat.dto.PongResponse;
import com.example.chat.service.OnlineUserService;
import com.example.chat.session.RoomBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.JsonNode;

@Component
@RequiredArgsConstructor
public class PingWsHandler implements WsMessageHandler {
    private final RoomBroadcaster broadcaster;
    private final OnlineUserService onlineUserService;

    @Override
    public String type() {
        return "ping";
    }

    @Override
    public void handle(
            WebSocketSession session,
            Long roomId,
            String nickname,
            JsonNode message
    ) {
        onlineUserService.refresh(roomId, session.getId());
        PongResponse pong = new PongResponse();
        broadcaster.sendTo(session, pong);
    }
}
