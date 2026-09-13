package com.example.chat.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Component
@RequiredArgsConstructor
public class WsMessageRouter {
    private final List<WsMessageHandler> handlers;

    public WsMessageHandler find(
            JsonNode message
    ) {
        String type = message.path("type").asString("");
        return handlers.stream()
                .filter(handler -> handler.type().equals(type))
                .findFirst()
                .orElse(null);
    }
}
