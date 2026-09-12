package com.example.chat.handler;

import com.example.chat.dto.ErrorResponse;
import com.example.chat.dto.WelcomeResponse;
import com.example.chat.service.OnlineUserService;
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
import tools.jackson.databind.exc.JsonNodeException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final RoomSessionRegistry registry;
    private final RoomBroadcaster broadcaster;
    private final OnlineUserService onlineUserService;
    private final WsMessageRouter router;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(
            WebSocketSession session
    ) throws Exception {
        Integer errorCode = (Integer) session.getAttributes().get(NicknameHandshakeInterceptor.ATTR_ERROR_CODE);
        if (errorCode != null) {
            session.close(new CloseStatus(errorCode));
            return;
        }
        Long roomId = (Long) session.getAttributes().get(NicknameHandshakeInterceptor.ATTR_ROOM_ID);
        String nickname = (String) session.getAttributes().get(NicknameHandshakeInterceptor.ATTR_NICKNAME);
        if (!registry.register(roomId, nickname, session)) {
            session.close(new CloseStatus(4002));
            return;
        }
        onlineUserService.refresh(roomId, session.getId());
        log.info("입장: room={} nickname={}", roomId, nickname);
        WelcomeResponse welcome = new WelcomeResponse(nickname, roomId);
        broadcaster.sendTo(session, welcome);
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message
    ) {
        Long roomId = (Long) session.getAttributes().get(NicknameHandshakeInterceptor.ATTR_ROOM_ID);
        String nickname = (String) session.getAttributes().get(NicknameHandshakeInterceptor.ATTR_NICKNAME);
        JsonNode json;
        try {
            json = objectMapper.readTree(message.getPayload());
        } catch (Exception notJson) {
            ErrorResponse error = new ErrorResponse("INVALID_JSON");
            broadcaster.sendTo(session, error);
            return;
        }
        WsMessageHandler handler = router.find(json);
        if (handler == null) {
            ErrorResponse error = new ErrorResponse("UNKNOWN_TYPE");
            broadcaster.sendTo(session, error);
            return;
        }
        try {
            handler.handle(session, roomId, nickname, json);
        } catch (IllegalArgumentException | JsonNodeException invalid) {
            ErrorResponse error = new ErrorResponse("INVALID_MESSAGE");
            broadcaster.sendTo(session, error);
        } catch (Exception unexpected) {
            log.error("처리 중 오류", unexpected);
            ErrorResponse error = new ErrorResponse("INTERNAL_ERROR");
            broadcaster.sendTo(session, error);
        }
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status
    ) {
        Long roomId = (Long) session.getAttributes().get(NicknameHandshakeInterceptor.ATTR_ROOM_ID);
        String nickname = (String) session.getAttributes().get(NicknameHandshakeInterceptor.ATTR_NICKNAME);
        if (roomId == null || nickname == null) return;
        if (!registry.remove(roomId, nickname, session)) return;
        onlineUserService.leave(roomId, session.getId());
        log.info("퇴장: room={} nickname={} code={}", roomId, nickname, status.getCode());
    }
}
