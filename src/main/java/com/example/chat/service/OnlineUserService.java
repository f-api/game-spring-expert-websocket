package com.example.chat.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OnlineUserService {
    private static final Duration KEY_TTL = Duration.ofSeconds(180);
    private static final long ALIVE_MS = 90_000L;
    private final StringRedisTemplate redisTemplate;

    public void refresh(
            Long roomId,
            String connectionId
    ) {
        String key = "chat:room:" + roomId + ":online";
        redisTemplate.opsForZSet().add(key, connectionId, System.currentTimeMillis() + ALIVE_MS);
        redisTemplate.expire(key, KEY_TTL);
    }

    public void leave(
            Long roomId,
            String connectionId
    ) {
        String key = "chat:room:" + roomId + ":online";
        redisTemplate.opsForZSet().remove(key, connectionId);
    }

    public Long onlineCount(
            Long roomId
    ) {
        String key = "chat:room:" + roomId + ":online";
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, System.currentTimeMillis());
        return redisTemplate.opsForZSet().zCard(key);
    }
}
