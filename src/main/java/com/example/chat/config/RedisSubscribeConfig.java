package com.example.chat.config;

import com.example.chat.service.ChatRelay;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
public class RedisSubscribeConfig {
    private final RedisConnectionFactory connectionFactory;
    private final ChatRelay chatRelay;

    @Bean
    public RedisMessageListenerContainer chatListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(chatRelay, new PatternTopic("chat:room:*"));
        return container;
    }
}
