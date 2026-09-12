package com.example.chat.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatResponse {
    private final String type = "chat";
    private final String sender;
    private final String content;
    private final String timestamp;
}
