package com.example.chat.controller;

import com.example.chat.service.OnlineUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OnlineUserController {
    private final OnlineUserService onlineUserService;

    @GetMapping("/rooms/{roomId}/online-count")
    public ResponseEntity<Long> onlineCount(
            @PathVariable Long roomId
    ) {
        return ResponseEntity.ok(onlineUserService.onlineCount(roomId));
    }
}
