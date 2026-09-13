package com.example.chat.controller;

import com.example.chat.dto.ChatResponse;
import com.example.chat.service.ChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatHistoryController {
    private final ChatService chatService;

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatResponse>> messages(
            @PathVariable Long roomId
    ) {
        return ResponseEntity.ok(chatService.recent(roomId));
    }
}
