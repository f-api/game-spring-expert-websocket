package com.example.chat.service;

import com.example.chat.dto.ChatResponse;
import com.example.chat.entity.ChatMessage;
import com.example.chat.repository.ChatMessageRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;

    public ChatResponse save(
            Long roomId,
            String sender,
            String content
    ) {
        String timestamp = LocalDateTime.now().toString();
        ChatMessage message = new ChatMessage(roomId, sender, content, timestamp);
        ChatMessage saved = chatMessageRepository.save(message);
        ChatResponse response = new ChatResponse(
                saved.getSender(), saved.getContent(), saved.getTimestamp());
        return response;
    }

    @Transactional(readOnly = true)
    public List<ChatResponse> recent(
            Long roomId
    ) {
        List<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderByIdAsc(roomId);
        List<ChatResponse> responses = messages.stream()
                .map(message -> new ChatResponse(message.getSender(), message.getContent(), message.getTimestamp()))
                .toList();
        return responses;
    }
}
