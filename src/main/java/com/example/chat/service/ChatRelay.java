package com.example.chat.service;

import com.example.chat.session.RoomBroadcaster;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
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
            String json
    ) {
        redisTemplate.convertAndSend(CHANNEL_PREFIX + roomId, json);
        log.info("발행: room={} {}", roomId, json);
    }

    @Override
    public void onMessage(
            Message message,
            byte[] pattern
    ) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        Long roomId = Long.valueOf(channel.substring(CHANNEL_PREFIX.length()));
        log.info("수신: room={} {}", roomId, payload);
        broadcaster.broadcastRaw(roomId, payload);
    }

    @Bean
    public RedisMessageListenerContainer chatListenerContainer(
            RedisConnectionFactory factory
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(this, new PatternTopic(CHANNEL_PREFIX + "*"));
        return container;
    }
}
