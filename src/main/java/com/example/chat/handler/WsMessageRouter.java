package com.example.chat.handler;

import tools.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class WsMessageRouter {
    private final Map<String, WsMessageHandler> handlers = new HashMap<>();

    public WsMessageRouter(
            List<WsMessageHandler> handlerList
    ) {
        for (WsMessageHandler handler : handlerList) {
            handlers.put(handler.type(), handler);
        }
    }

    public WsMessageHandler find(
            JsonNode message
    ) {
        return handlers.get(message.path("type").asString(""));
    }
}
