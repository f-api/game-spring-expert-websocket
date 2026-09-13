package com.example.chat.service;

import com.example.chat.session.RoomBroadcaster;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRelay implements MessageListener {
    private static final String CHANNEL_PREFIX = "chat:room:";
    private final StringRedisTemplate redisTemplate;
    private final RoomBroadcaster broadcaster;

    public void publish(
            Long roomId,
            String jsonMessage
    ) {
        redisTemplate.convertAndSend(CHANNEL_PREFIX + roomId, jsonMessage);
        log.info("발행: room={} {}", roomId, jsonMessage);
    }

    @Override
    public void onMessage(
            Message message,
            byte[] pattern
    ) {
        String channelName = new String(message.getChannel(), StandardCharsets.UTF_8);
        String jsonMessage = new String(message.getBody(), StandardCharsets.UTF_8);
        Long roomId = Long.valueOf(channelName.substring(CHANNEL_PREFIX.length()));
        log.info("수신: room={} {}", roomId, jsonMessage);
        broadcaster.broadcastJson(roomId, jsonMessage);
    }
}
