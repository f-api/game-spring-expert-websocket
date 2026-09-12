package com.example.chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "chat_messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long roomId;
    private String sender;
    private String content;
    private String timestamp;

    public ChatMessage(
            Long roomId,
            String sender,
            String content,
            String timestamp
    ) {
        this.roomId = roomId;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }
}
