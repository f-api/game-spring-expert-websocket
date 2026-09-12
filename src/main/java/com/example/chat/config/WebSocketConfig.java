package com.example.chat.config;

import com.example.chat.handler.ChatWebSocketHandler;
import com.example.chat.handler.EchoWebSocketHandler;
import com.example.chat.session.NicknameHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final EchoWebSocketHandler echoHandler;
    private final ChatWebSocketHandler chatHandler;
    private final NicknameHandshakeInterceptor interceptor;

    @Override
    public void registerWebSocketHandlers(
            WebSocketHandlerRegistry registry
    ) {
        registry.addHandler(echoHandler, "/ws/echo");
        registry.addHandler(chatHandler, "/ws/rooms/{roomId}")
                .addInterceptors(interceptor)
                .setAllowedOriginPatterns("*");
    }
}
