package com.example.chat.session;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class RoomSessionRegistry {
    private final Map<Long, Map<String, WebSocketSession>> rooms = new ConcurrentHashMap<>();

    public boolean register(
            Long roomId,
            String nickname,
            WebSocketSession session
    ) {
        Map<String, WebSocketSession> sessionsByNickname = rooms.computeIfAbsent(roomId, id -> new ConcurrentHashMap<>());
        return sessionsByNickname.putIfAbsent(nickname, session) == null;
    }

    public Collection<WebSocketSession> sessions(
            Long roomId
    ) {
        Map<String, WebSocketSession> sessionsByNickname = rooms.get(roomId);
        return sessionsByNickname == null ? List.of() : sessionsByNickname.values();
    }

    public boolean remove(
            Long roomId,
            String nickname,
            WebSocketSession session
    ) {
        Map<String, WebSocketSession> sessionsByNickname = rooms.get(roomId);
        return sessionsByNickname.remove(nickname, session);
    }
}
