package com.example.chat.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final String type = "error";
    private final String code;
}
