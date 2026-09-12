package com.example.chat.session;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class NicknameHandshakeInterceptor implements HandshakeInterceptor {
    public static final String ATTR_NICKNAME = "nickname";
    public static final String ATTR_ROOM_ID = "roomId";
    public static final String ATTR_ERROR_CODE = "errorCode";
    private static final Pattern NICKNAME = Pattern.compile("^[A-Za-z0-9_]{2,12}$");
    private static final Pattern ROOM_PATH = Pattern.compile("/ws/rooms/(\\d+)");

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler handler,
            Map<String, Object> attributes
    ) {
        String query = request.getURI().getQuery();
        String nickname = (query != null && query.startsWith("nickname="))
                ? query.substring("nickname=".length()) : null;

        Matcher matcher = ROOM_PATH.matcher(request.getURI().getPath());
        Long roomId = matcher.matches() ? Long.valueOf(matcher.group(1)) : null;

        // 거절 사유를 close code 로 알리려고 핸드셰이크는 통과시키고 사유만 담습니다.
        if (nickname == null || !NICKNAME.matcher(nickname).matches()) {
            attributes.put(ATTR_ERROR_CODE, 4000);
            return true;
        }
        if (roomId == null || roomId < 1 || roomId > 3) {
            attributes.put(ATTR_ERROR_CODE, 4001);
            return true;
        }

        attributes.put(ATTR_NICKNAME, nickname);
        attributes.put(ATTR_ROOM_ID, roomId);
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler handler,
            Exception exception
    ) {
    }
}
