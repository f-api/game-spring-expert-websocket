package com.example.chat.handler;

import com.example.chat.session.NicknameHandshakeInterceptor;
import com.example.chat.session.RoomBroadcaster;
import com.example.chat.session.RoomSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final RoomSessionRegistry registry;
    private final RoomBroadcaster broadcaster;
    private final WsMessageRouter router;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(
            WebSocketSession session
    ) throws Exception {
        Long roomId = (Long) session.getAttributes().get(NicknameHandshakeInterceptor.ATTR_ROOM_ID);
        String nickname = (String) session.getAttributes().get(NicknameHandshakeInterceptor.ATTR_NICKNAME);
        if (!registry.register(roomId, nickname, session)) {
            CloseStatus status = new CloseStatus(4002);
            session.close(status);
            return;
        }
        log.info("입장: room={} nickname={}", roomId, nickname);
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message
    ) {
        Long roomId = (Long) session.getAttributes().get(NicknameHandshakeInterceptor.ATTR_ROOM_ID);
        String nickname = (String) session.getAttributes().get(NicknameHandshakeInterceptor.ATTR_NICKNAME);
        JsonNode parsedMessage;
        try {
            parsedMessage = objectMapper.readTree(message.getPayload());
        } catch (Exception notJson) {
            return;
        }
        WsMessageHandler handler = router.find(parsedMessage);
        if (handler == null) {
            return;
        }
        handler.handle(session, roomId, nickname, parsedMessage);
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status
    ) {
        Long roomId = (Long) session.getAttributes().get(NicknameHandshakeInterceptor.ATTR_ROOM_ID);
        String nickname = (String) session.getAttributes().get(NicknameHandshakeInterceptor.ATTR_NICKNAME);
        if (roomId == null || nickname == null) {
            return;
        }
        if (!registry.remove(roomId, nickname, session)) {
            return;
        }
        log.info("퇴장: room={} nickname={} code={}", roomId, nickname, status.getCode());
    }
}
