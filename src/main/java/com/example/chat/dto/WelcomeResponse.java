package com.example.chat.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WelcomeResponse {
    private final String type = "welcome";
    private final String nickname;
    private final Long roomId;
}
